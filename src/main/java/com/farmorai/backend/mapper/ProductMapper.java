package com.farmorai.backend.mapper;

import com.farmorai.backend.dto.ProductDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ProductMapper {
    void registerProduct(ProductDto productDto);

    List<ProductDto> getProductList();

    Optional<ProductDto> getProductById(Long productId);

    int decreaseStock(@Param("productId") Long productId,@Param("quantity") int quantity);

    void batchRegisterProduct(List<ProductDto> productList);

    List<ProductDto> searchProductList(@Param("keyword") String keyword,
                                       @Param("size") int size,
                                       @Param("offset") int offset);

    int getProductCount(@Param("keyword") String keyword);
}
