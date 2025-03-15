package com.farmorai.backend.service;

import com.farmorai.backend.domain.ProductDoc;
import com.farmorai.backend.dto.ProductDto;
import com.farmorai.backend.mapper.ProductMapper;
import com.farmorai.backend.repository.ProductDocRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

    private final ProductMapper productMapper;
//    private final ProductDocRepository productDocRepository;

    public void registerProduct(ProductDto productDto) {
        // 상품 등록 로직
        productMapper.registerProduct(productDto);

    }

//    public List<ProductDoc> getProductDocList() {
//        return productDocRepository.getProductList();
//    }

//    public List<ProductDoc> getAllProducts() {
//        Iterable<ProductDoc> iterable = productDocRepository.findAll();
//        return StreamSupport.stream(iterable.spliterator(), false)
//                .collect(Collectors.toList());
//    }

    public List<ProductDto> getProductList() {
        return productMapper.getProductList();
    }

    public Optional<ProductDto> getProductById(Long productId) {
        return productMapper.getProductById(productId);
    }
}
