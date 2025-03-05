package com.farmorai.backend.service;

import com.farmorai.backend.dto.ProductDto;
import com.farmorai.backend.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

    private final ProductMapper productMapper;

    public void registerProduct(ProductDto productDto) {
        // 상품 등록 로직
        productMapper.registerProduct(productDto);

    }

    public List<ProductDto> getProductList() {
        return productMapper.getProductList();
    }

    public Optional<ProductDto> getProductById(Long productId) {
        return productMapper.getProductById(productId);
    }
}
