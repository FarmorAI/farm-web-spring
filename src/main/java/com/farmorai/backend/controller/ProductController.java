package com.farmorai.backend.controller;

import com.farmorai.backend.dto.ProductDto;
import com.farmorai.backend.service.ProductService;
import com.farmorai.backend.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final S3Service s3Service;

    @PostMapping
    public ResponseEntity<String> registerProduct(@RequestPart("product") ProductDto productDto,
                                                  @RequestPart("file") MultipartFile file) {
        productDto.setImageUrl(s3Service.uploadFile(file));
        productService.registerProduct(productDto);
        return ResponseEntity.ok("✅ 상품이 등록되었습니다.");
    }



}
