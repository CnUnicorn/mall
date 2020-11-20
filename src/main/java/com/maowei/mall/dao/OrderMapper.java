package com.maowei.mall.dao;

import com.maowei.mall.pojo.Order;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    List<Order> selectByUid(Integer uid);

    Order selectByOrderNo(Long uid);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);
}