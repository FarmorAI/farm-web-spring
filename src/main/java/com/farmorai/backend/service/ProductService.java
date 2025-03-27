package com.farmorai.backend.service;

import com.farmorai.backend.domain.ProductDoc;
import com.farmorai.backend.dto.ProductDto;
import com.farmorai.backend.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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

//    public List<ProductDoc> searchByKeyword(String keyword) {
//        return productDocRepository.searchByKeyword(keyword);
//    }

    public List<ProductDto> getProductList() {
        return productMapper.getProductList();
    }

    public Optional<ProductDto> getProductById(Long productId) {
        return productMapper.getProductById(productId);
    }

    public void decreaseStock(Long productId, int quantity) {
        if(quantity <= 0) {
            throw new IllegalArgumentException("감소할 수량은 0보다 커야 합니다.");
        }
        int updateRows = productMapper.decreaseStock(productId, quantity);
        if (updateRows == 0) {
            throw new IllegalStateException("재고가 부족합니다.");
        }
    }

    public Page<ProductDto> searchProductList(String keyword, int page, int size) {
        int offset = (page - 1) * size;
        List<ProductDto> products = productMapper.searchProductList(keyword, size, offset);
        int totalCount = productMapper.getProductCount(keyword);

        return new PageImpl<>(products, PageRequest.of(page, size), totalCount);
    }
}
