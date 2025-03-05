package com.farmorai.backend.mapper;

import com.farmorai.backend.dto.ProductDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ProductMapper {
    void registerProduct(ProductDto productDto);

    List<ProductDto> getProductList();

    Optional<ProductDto> getProductById(Long productId);
}
