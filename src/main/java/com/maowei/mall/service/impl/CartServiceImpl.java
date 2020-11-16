package com.maowei.mall.service.impl;

import com.google.gson.Gson;
import com.maowei.mall.dao.ProductMapper;
import com.maowei.mall.enums.ProductStatusEnum;
import com.maowei.mall.enums.ResponseEnum;
import com.maowei.mall.form.CartAddForm;
import com.maowei.mall.form.CartUpdateForm;
import com.maowei.mall.pojo.Cart;
import com.maowei.mall.pojo.Product;
import com.maowei.mall.service.ICartService;
import com.maowei.mall.vo.CartProductVo;
import com.maowei.mall.vo.CartVo;
import com.maowei.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class CartServiceImpl implements ICartService {

    private static final String CART_REDIS_KEY_TEMPLATE = "cart_%d";

    private Gson gson = new Gson();

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private StringRedisTemplate redisTemplate; // spring封装好的redis对象

    @Override
    public ResponseVo<CartVo> add(Integer uid, CartAddForm cartAddForm) {
        Integer quantity = 1;
        Product product = productMapper.selectByPrimaryKey(cartAddForm.getProductId());
        // 1.判断商品是否存在
        if (product == null) {
            return ResponseVo.error(ResponseEnum.PRODUCT_NOT_EXIST);
        }
        // 2.商品是否正常在售
        if (!product.getStatus().equals(ProductStatusEnum.ON_SALE.getCode())) {
            return ResponseVo.error(ResponseEnum.PRODUCT_OFF_SALE_OR_DELETE);
        }

        // 3.判断商品库存是否充足
        if (product.getStock() <= 0) {
            return ResponseVo.error(ResponseEnum.PRODUCT_STOCK_ERROR);
        }

        // 写入到Redis
        // key: cart_uid
        // 由于CartProductVo中的商品名称、子标题、价格等属性可能会在数据库中变动，
        // 所以Redis中只存放productId，quantity和productSelected三个参数，
        // 通过这些再去数据库中查找对应的商品信息。
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid); // 购物车uid表名
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String value = opsForHash.get(redisKey, Integer.toString(product.getId()));

        Cart cart;
        if (StringUtils.isEmpty(value)) {
            // 如果商品不存在，新增商品，isEmpty在对象为null或长度为0时，返回true
            cart = new Cart(product.getId(), quantity, cartAddForm.getSelected());
        }else {
            // 商品存在，数量加一
            cart = gson.fromJson(value, Cart.class);
            cart.setQuantity(cart.getQuantity() + 1);
        }

        opsForHash.put(redisKey,
                Integer.toString(product.getId()),
                gson.toJson(cart));
        return null;
    }

//    @Override
//    public ResponseVo<CartVo> list(Integer uid) {
//        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
//        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);
//        Map<String, String> entries = opsForHash.entries(redisKey);
//
//        CartVo cartVo = new CartVo();
//        Integer totalQuantity = 0;
//        boolean selectAll = true;
//        BigDecimal totalPrice = BigDecimal.ZERO;
//        List<CartProductVo> cartVoList = new ArrayList<>();
//        // TODO 需要优化，不要在for循环里查询sql，使用mysql里的in
//        for (Map.Entry<String, String> entry : entries.entrySet()) {
//            int productId = Integer.parseInt(entry.getKey());
//            Cart cart = gson.fromJson(entry.getValue(), Cart.class);
//
//            Product product = productMapper.selectByPrimaryKey(productId);
//            if (product != null) {
//                CartProductVo cartProductVo = new CartProductVo(productId,
//                        cart.getQuantity(), product.getName(), product.getSubtitle(),
//                        product.getMainImage(), product.getPrice(), product.getStatus(),
//                        product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())),
//                        product.getStock(), cart.getProductSelected());
//                cartVoList.add(cartProductVo);
//
//                if (!cart.getProductSelected()) {
//                    selectAll = false;
//                }
//
//                // 只计算选中商品的总价
//                if (cart.getProductSelected()) {
//                    totalPrice = totalPrice.add(cartProductVo.getProductTotalPrice());
//                }
//
//            }
//            totalQuantity += cart.getQuantity();
//        }
//
//        cartVo.setCartTotalQuantity(totalQuantity);
//        cartVo.setSelectAll(selectAll);
//        cartVo.setCartTotalPrice(totalPrice);
//        cartVo.setCartProductVoList(cartVoList);
//
//        return ResponseVo.success(cartVo);
//    }

    @Override
    public ResponseVo<CartVo> list(Integer uid) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        Map<String, String> entries = opsForHash.entries(redisKey);
        Set<String> idSet = entries.keySet();
        // Redis中存储的所有商品id，在数据库中查找出来的商品信息
        // 只查询一次数据库
        List<Product> products = productMapper.selectByProductIdSet(idSet);

        CartVo cartVo = new CartVo();
        Integer totalQuantity = 0;
        boolean selectAll = true;
        BigDecimal totalPrice = BigDecimal.ZERO;
        List<CartProductVo> cartProductVoList = new ArrayList<>();

        for (Map.Entry<String, String> entry : entries.entrySet()) {
            int productId = Integer.parseInt(entry.getKey());
            Cart cart = gson.fromJson(entry.getValue(), Cart.class);
            for (Product product : products) {
                if (product.getId().equals(productId)) {
                    CartProductVo cartProductVo = new CartProductVo(productId,
                            cart.getQuantity(), product.getName(), product.getSubtitle(),
                            product.getMainImage(), product.getPrice(), product.getStatus(),
                            product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())),
                            product.getStock(), cart.getProductSelected());

                    cartProductVoList.add(cartProductVo);

                    if (!cart.getProductSelected()) {
                        selectAll = false;
                    }

                    // 只计算选中商品的总价
                    if (cart.getProductSelected()) {
                        totalPrice = totalPrice.add(cartProductVo.getProductTotalPrice());
                    }
                }

            }
            totalQuantity += cart.getQuantity();
        }

        cartVo.setCartTotalQuantity(totalQuantity);
        cartVo.setSelectAll(selectAll);
        cartVo.setCartTotalPrice(totalPrice);
        cartVo.setCartProductVoList(cartProductVoList);

        return ResponseVo.success(cartVo);
    }

    @Override
    public ResponseVo<CartVo> update(Integer uid, Integer productId, CartUpdateForm cartUpdateForm) {
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid); // 购物车uid表名
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String value = opsForHash.get(redisKey, Integer.toString(productId));

        if (StringUtils.isEmpty(value)) {
            // 如果商品不存在，报错
            return ResponseVo.error(ResponseEnum.CART_PRODUCT_NOT_EXIST);
        }
        // 商品存在，更新内容
        Cart cart = gson.fromJson(value, Cart.class);
        if (cartUpdateForm.getQuantity() != null
                && cartUpdateForm.getQuantity() >= 0) {
            cart.setQuantity(cartUpdateForm.getQuantity());
        }
        if (cartUpdateForm.getSelected() != null) {
            cart.setProductSelected(cartUpdateForm.getSelected());
        }
        // 更新Redis数据库
        opsForHash.put(redisKey, Integer.toString(productId), gson.toJson(cart));

        return list(uid);
    }

    @Override
    public ResponseVo<CartVo> delete(Integer uid, Integer productId) {
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        HashOperations<String, String , String> opsForHash = redisTemplate.opsForHash();

        String value = opsForHash.get(redisKey, Integer.toString(productId));
        if (StringUtils.isEmpty(value)) {
            return ResponseVo.error(ResponseEnum.CART_PRODUCT_NOT_EXIST);
        }
        opsForHash.delete(redisKey, Integer.toString(productId));

        return list(uid);
    }

    /**
     * 所有商品设置成未选中
     * @param uid
     * @return
     */
    @Override
    public ResponseVo<CartVo> unSelectAll(Integer uid) {
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();

        for (Cart cart : listForCart(uid)) {
            cart.setProductSelected(false);
            opsForHash.put(redisKey, Integer.toString(cart.getProductId()), gson.toJson(cart));
        }

        return list(uid);
    }

    /**
     * 得到购物车所有商品的数量总和
     * @param uid
     * @return
     */
    @Override
    public ResponseVo<Integer> sum(Integer uid) {
        Integer sum = 0;
        for (Cart cart : listForCart(uid)) {
            sum += cart.getQuantity();
        }

        return ResponseVo.success(sum);
    }

    /**
     * 所有商品设置成选中状态
     * @param uid
     * @return
     */
    @Override
    public ResponseVo<CartVo> selectAll(Integer uid) {
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();

        for (Cart cart : listForCart(uid)) {
            cart.setProductSelected(true);
            opsForHash.put(redisKey, Integer.toString(cart.getProductId()), gson.toJson(cart));
        }

        return list(uid);
    }

    /**
     * 遍历返回表中所有的Cart对象
     * @param uid
     * @return
     */
    public List<Cart> listForCart(Integer uid) {
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        Map<String, String> entries = opsForHash.entries(redisKey);
        List<Cart> cartList = new ArrayList<>();
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            cartList.add(gson.fromJson(entry.getValue(), Cart.class));
        }

        return cartList;
    }
}
