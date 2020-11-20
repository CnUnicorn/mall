package com.maowei.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.maowei.mall.dao.OrderItemMapper;
import com.maowei.mall.dao.OrderMapper;
import com.maowei.mall.dao.ProductMapper;
import com.maowei.mall.dao.ShippingMapper;
import com.maowei.mall.enums.OrderStatusEnum;
import com.maowei.mall.enums.PaymentTypeEnum;
import com.maowei.mall.enums.ProductStatusEnum;
import com.maowei.mall.enums.ResponseEnum;
import com.maowei.mall.pojo.*;
import com.maowei.mall.service.ICartService;
import com.maowei.mall.service.IOrderService;
import com.maowei.mall.vo.OrderItemVo;
import com.maowei.mall.vo.OrderVo;
import com.maowei.mall.vo.ResponseVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements IOrderService {

    @Autowired
    private ShippingMapper shippingMapper;

    @Autowired
    private ICartService cartService;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;


    /**
     *
     * @param uid 用户id
     * @param shippingId 收货地址id，shipping表中的主键
     * @return
     */
    @Override
    @Transactional
    public ResponseVo<OrderVo> create(Integer uid, Integer shippingId) {
        // 1.收货地址校验（最后要返回收获地址，一定会从数据库中查出来），保证是当前用户的收货地址
        Shipping shipping = shippingMapper.selectByIdAndUid(shippingId, uid);
        if (shipping == null) {
            return ResponseVo.error(ResponseEnum.SHIPPING_NOT_EXIST);
        }

        // 2.获取购物车（从Redis取出，且使用stream取出选中的商品），校验（是否有商品、库存是否充足）
        List<Cart> cartList = cartService.listForCart(uid)
                .stream()
                .filter(Cart::getProductSelected)
                .collect(Collectors.toList());
        if (cartList.isEmpty()) { // 校验购物车中有没有选中的商品
            return ResponseVo.error(ResponseEnum.CART_SELECTED_IS_EMPTY);
        }

        // 获取购物车cartList里的所有productId，因为productId一定不会重复所以用set存储
        Set<String> productIdSet = new HashSet<>();
        for (Cart cart : cartList) {
            productIdSet.add(cart.getProductId().toString());
        }
        // 获取数据库中对应productId的商品信息
        List<Product> productList = productMapper.selectByProductIdSet(productIdSet);
        // 利用Stream把List转换成Map，方便查找，否则要写两个for循环
        Map<Integer, Product> map = productList.stream()
                .collect(Collectors.toMap(Product::getId, product -> product));

        Long orderNo = generateOrderNo(); // 生成订单号
        List<OrderItem> orderItemList = new ArrayList<>(); // 存放orderItem的列表
        for (Cart cart : cartList) { // 根据productId查数据库，是否有商品和库存
            Product product = map.get(cart.getProductId());
            // 商品是否存在
            if (product == null) {
                return ResponseVo.error(ResponseEnum.PRODUCT_NOT_EXIST,
                        "商品不存在. productId = " + cart.getProductId());
            }
            // 商品是否下架，根据业务要求，返回给客户通知，所以在sql中不需要排除已经下架的商品
            if (!product.getStatus().equals(ProductStatusEnum.ON_SALE.getCode())) {
                return ResponseVo.error(ResponseEnum.PRODUCT_OFF_SALE_OR_DELETE,
                        "商品不是在售状态. " + product.getName());
            }
            // 库存是否充足
            if (product.getStock() < cart.getQuantity()) {
                return ResponseVo.error(ResponseEnum.PRODUCT_STOCK_ERROR,
                        "商品库存不正确. " + product.getName());
            }

            OrderItem orderItem = buildOrderItem(uid, orderNo, cart.getQuantity(), product);
            orderItemList.add(orderItem);

            // 减库存
            product.setStock(product.getStock() - cart.getQuantity());
            int row = productMapper.updateByPrimaryKeySelective(product);
            if (row <= 0) {
                return ResponseVo.error(ResponseEnum.ERROR);
            }
        }

        // 3.计算总价，只计算选中的商品（实际上就是生成Order对象）
        // 4.生成订单，入库：Order表，OrderItem表，事务控制，保证两个表数据都同时写入了，不会出现数据不同步的情况
        Order order = buildOrder(uid, orderNo, shippingId, orderItemList);
        int rowForOrder = orderMapper.insertSelective(order); // 返回的row表示影响的行数
        if (rowForOrder <= 0) {
            return ResponseVo.error(ResponseEnum.ERROR);
        }
        int rowForOrderItem = orderItemMapper.batchInsert(orderItemList);
        if (rowForOrderItem <= 0) {
            return ResponseVo.error(ResponseEnum.ERROR);
        }

        // 5.更新购物车（删掉选中的商品）
        // Redis也有事务（打包命令），但是不能回滚
        for (Cart cart : cartList) {
            cartService.delete(uid, cart.getProductId());
        }

        // 7.构造OrderVo对象
        OrderVo orderVo = buildOrderVo(order, orderItemList, shipping);
        return ResponseVo.success(orderVo);
    }

    @Override
    public ResponseVo<PageInfo> list(Integer uid, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectByUid(uid); // 同一个用户的所有订单

        Set<Long> orderNoSet = orderList.stream().map(Order::getOrderNo).collect(Collectors.toSet());
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNoSet(orderNoSet); // 所有订单下的所有OrderItem对象列表，对应的订单号可能不同
        Map<Long, List<OrderItem>> orderItemMap = orderItemList.stream()
                .collect(Collectors.groupingBy(OrderItem::getOrderNo)); // 将orderItemList转成Map，key-orderNo，value-对应订单下的orderItemList

        Set<Integer> shippingIdSet = orderList.stream().map(Order::getShippingId).collect(Collectors.toSet());
        List<Shipping> shippingList = shippingMapper.selectByShippingIdSet(shippingIdSet); // 所有订单下的所有收获地址，对应的订单号可能不同
        Map<Integer, Shipping> shippingMap = shippingList.stream()
                .collect(Collectors.toMap(Shipping::getId, shipping -> shipping)); // key-shippingId,value-对应的Shipping对象

        List<OrderVo> orderVoList = new ArrayList<>();
        for (Order order : orderList) {
            OrderVo orderVo = buildOrderVo(order,
                    orderItemMap.get(order.getOrderNo()),
                    shippingMap.get(order.getShippingId()));
            orderVoList.add(orderVo);
        }
        PageInfo pageInfo = new PageInfo(orderList);
        pageInfo.setList(orderVoList);

        return ResponseVo.success(pageInfo);
    }

    /**
     * 查找某个订单的详情
     * @param uid
     * @param orderNo
     * @return
     */
    @Override
    public ResponseVo<OrderVo> detail(Integer uid, Long orderNo) {
        // 先校验订单是否属于该用户
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null || !order.getUserId().equals(uid)) {
            return ResponseVo.error(ResponseEnum.ORDER_NOT_EXIST);
        }

        Set<Long> orderNoSet = new HashSet<>();
        orderNoSet.add(orderNo);
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNoSet(orderNoSet);

        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());

        OrderVo orderVo = buildOrderVo(order, orderItemList, shipping);

        return ResponseVo.success(orderVo);
    }

    @Override
    public ResponseVo cancel(Integer uid, Long orderNo) {
        // 校验订单是否属于该用户
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null || !order.getUserId().equals(uid)) {
            return ResponseVo.error(ResponseEnum.ORDER_NOT_EXIST);
        }

        // 设定只有在订单状态是未付款的时候才可以取消订单（只考虑了简单的情况），实际上付款后也可以取消，只不过需要退款
        if (!order.getStatus().equals(OrderStatusEnum.NOT_PAY.getCode())) {
            return ResponseVo.error(ResponseEnum.ORDER_STATUS_ERROR);
        }
        // 如果是未支付状态，将订单状态设置成已取消，类似软删除，数据还在
        order.setStatus(OrderStatusEnum.CANCELED.getCode());
        order.setCloseTime(new Date()); // 关闭时间设置成当前时间
        int row = orderMapper.updateByPrimaryKeySelective(order);
        if (row <= 0) {
            // 数据库更新未成功
            return ResponseVo.error(ResponseEnum.ERROR);
        }

        return ResponseVo.success();
    }

    @Override
    public void paid(Long orderNo) {
        // 校验订单是否属于该用户
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new RuntimeException(ResponseEnum.ORDER_NOT_EXIST.getDesc() + " 订单id：" + orderNo);
        }

        // 设定只有在“未付款”状态，才能改变成已付款状态
        if (!order.getStatus().equals(OrderStatusEnum.NOT_PAY.getCode())) {
            throw new RuntimeException(ResponseEnum.ORDER_STATUS_ERROR.getDesc() + " 订单id：" + orderNo);
        }
        order.setStatus(OrderStatusEnum.PAID.getCode());
        order.setPaymentTime(new Date()); // 付款时间设置成当前时间
        int row = orderMapper.updateByPrimaryKeySelective(order);
        if (row <= 0) {
            // 数据库更新未成功
            throw new RuntimeException("将订单更新为已支付状态失败，订单id：" + orderNo);
        }
    }

    private OrderVo buildOrderVo(Order order, List<OrderItem> orderItemList, Shipping shipping) {
        List<OrderItemVo> orderItemVoList = orderItemList.stream().map(e -> {
            OrderItemVo orderItemVo = new OrderItemVo();
            BeanUtils.copyProperties(e, orderItemVo);
            return orderItemVo;
        }).collect(Collectors.toList());

        OrderVo orderVo = new OrderVo();
        BeanUtils.copyProperties(order, orderVo);
        orderVo.setOrderItemVoList(orderItemVoList);
        if (shipping.getId() != null) {
            orderVo.setShippingId(shipping.getId());
            orderVo.setShippingVo(shipping);
        }

        return orderVo;
    }

    private Order buildOrder(Integer uid,
                             Long orderNo,
                             Integer shippingId,
                             List<OrderItem> orderItemList) {
        BigDecimal payment = orderItemList.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(uid);
        order.setShippingId(shippingId);
        order.setPayment(payment); // 某个订单的总价，所有orderItem的总价之和
        order.setPaymentType(PaymentTypeEnum.PAY_ONLINE.getCode()); // 支付类型,1-在线支付,2-货到付款
        order.setPostage(0); // 暂时没有涉及到运费，设成0
        order.setStatus(OrderStatusEnum.NOT_PAY.getCode());
        return order;
    }

    /**
     * 使用时间戳加一个三位随机数，生成简单的订单号
     * 企业级：分布式唯一id/主键，目前没有最好的方案
     * @return
     */
    private Long generateOrderNo() {
        return System.currentTimeMillis() + new Random().nextInt(999);
    }

    private OrderItem buildOrderItem(Integer uid, Long orderNo, Integer quantity, Product product) {
        OrderItem item = new OrderItem();
        item.setUserId(uid);
        item.setOrderNo(orderNo);
        item.setProductId(product.getId());
        item.setProductName(product.getName());
        item.setProductImage(product.getMainImage());
        item.setCurrentUnitPrice(product.getPrice());
        item.setQuantity(quantity);
        item.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        return item;
    }
}
