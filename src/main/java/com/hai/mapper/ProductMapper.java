package com.hai.mapper;

import com.hai.model.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * Created by Administrator on 2017/11/13.
 */
@Component
@Mapper
public interface ProductMapper {
    Product select(@Param("id") int id);

    void update(Product product);
}
