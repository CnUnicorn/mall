package com.maowei.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.maowei.mall.dao.ShippingMapper;
import com.maowei.mall.enums.ResponseEnum;
import com.maowei.mall.form.ShippingForm;
import com.maowei.mall.pojo.Shipping;
import com.maowei.mall.service.IShippingService;
import com.maowei.mall.vo.ResponseVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class IShippingServiceImpl implements IShippingService {

    @Autowired
    private ShippingMapper shippingMapper;

    /**
     * 添加收货地址
     * @param uid userId,用户id
     * @param shippingForm
     * @return
     */
    @Override
    public ResponseVo<Map<String, Integer>> add(Integer uid, ShippingForm shippingForm) {
        Shipping shipping = new Shipping();
        BeanUtils.copyProperties(shippingForm, shipping);
        shipping.setUserId(uid);
        int ifSuccessWrite = shippingMapper.insertSelective(shipping);
        if (ifSuccessWrite == 0) { // 如果没有成功写入数据库
            return ResponseVo.error(ResponseEnum.ERROR);
        }

        // 这边返回数据的结构比较简单，所以直接返回一个Map，
        // 如果字段比较多，还是需要新建一个Vo对象
        Map<String, Integer> map = new HashMap<>();
        map.put("shippingId", shipping.getId());
        return ResponseVo.success(map);
    }

    /**
     * 保证是当前用户在删除收货地址，所以要同时传入uid和shippingId
     * 如果只传入shippingId，其他人只要知道了shippingId就可以删除其他用户的收货地址了
     * @param uid
     * @param shippingId
     * @return
     */
    @Override
    public ResponseVo delete(Integer shippingId, Integer uid) {
        int ifDeleteSuccess = shippingMapper.deleteByIdAndUid(shippingId, uid);
        if (ifDeleteSuccess == 0) {
            return ResponseVo.error(ResponseEnum.DELETE_SHIPPING_FAIL);
        }
        return ResponseVo.success();
    }

    @Override
    public ResponseVo update(Integer uid, Integer shippingId, ShippingForm shippingForm) {
        Shipping shipping = new Shipping();
        BeanUtils.copyProperties(shippingForm, shipping);
        shipping.setUserId(uid);
        shipping.setId(shippingId);
        int ifUpdateSuccess = shippingMapper.updateByPrimaryKeySelective(shipping);
        if (ifUpdateSuccess == 0) {
            return ResponseVo.error(ResponseEnum.ERROR);
        }
        return ResponseVo.success();
    }

    @Override
    public ResponseVo<PageInfo> list(Integer uid, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Shipping> shippings = shippingMapper.selectByUid(uid);
        PageInfo pageInfo = new PageInfo<>(shippings);
        return ResponseVo.success(pageInfo);
    }
}
