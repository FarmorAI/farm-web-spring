package com.farmorai.backend.mapper;

import com.farmorai.backend.dto.ProductDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper {
    void registerProduct(ProductDto productDto);
}
