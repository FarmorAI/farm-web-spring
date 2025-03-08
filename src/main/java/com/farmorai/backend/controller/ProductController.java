package com.farmorai.backend.controller;

import com.farmorai.backend.dto.ProductDto;
import com.farmorai.backend.dto.response.ApiResponse;
import com.farmorai.backend.service.ProductService;
import com.farmorai.backend.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;


@Slf4j
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final S3Service s3Service;

    @GetMapping("/list")
    public ResponseEntity<List<ProductDto>> getProductList() {
        return ResponseEntity.ok(productService.getProductList());
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductDto>> getProductById(@PathVariable Long productId) {
        Optional<ProductDto> product = productService.getProductById(productId);
        return ResponseEntity.ok(new ApiResponse<>(200,"상품 조회 성공", product.orElseThrow()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<String>> registerProduct(@RequestPart("product") ProductDto productDto,
                                                  @RequestPart("file") MultipartFile file) {
        log.info("productDto = {}",productDto);
        log.info("file = {}",file);
        productDto.setImageUrl(s3Service.uploadFile(file));
        productService.registerProduct(productDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "상품 등록 성공", "success"));
    }



}
