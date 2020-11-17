package com.maowei.mall.dao;

import com.maowei.mall.pojo.Shipping;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ShippingMapper {
    int deleteByPrimaryKey(Integer id);

    int deleteByIdAndUid(@Param("shippingId") Integer shippingId,
                         @Param("uid") Integer uid);

    int insert(Shipping record);

    int insertSelective(Shipping record);

    Shipping selectByPrimaryKey(Integer id);

    List<Shipping> selectByUid(Integer uid);

    int updateByPrimaryKeySelective(Shipping record);

    int updateByPrimaryKey(Shipping record);
}