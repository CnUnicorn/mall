package com.maowei.mall.dao;


import com.maowei.mall.pojo.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;


@Mapper            // 标记这个接口是Mybatis映射接口
@Repository        // 注册bean，创建CategoryMapper接口的实体类
public interface CategoryMapper {

    @Select("SELECT * FROM mall_category WHERE id = #{id}")
    Category findById(@Param("id") Integer id);

    Category queryById(Integer id);

}
