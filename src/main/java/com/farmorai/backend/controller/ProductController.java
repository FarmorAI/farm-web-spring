package com.farmorai.backend.controller;

import com.farmorai.backend.domain.ProductDoc;
import com.farmorai.backend.dto.ProductDto;
import com.farmorai.backend.global.response.ResponseApi;
import com.farmorai.backend.service.ProductService;
import com.farmorai.backend.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
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
    public ResponseEntity<ResponseApi<ProductDto>> getProductById(@PathVariable Long productId) {
        Optional<ProductDto> product = productService.getProductById(productId);
        return ResponseEntity.ok(ResponseApi.success("상품 조회 성공", product.orElseThrow()));
    }

//    @GetMapping
//    public ResponseApi<List<ProductDoc>> getProductDocList() {
//        List<ProductDoc> productDocList = productService.getProductDocList();
//        log.info("getProductDocList: {}", productDocList);
//        return ResponseApi.success("상품 조회 성공", productService.getProductDocList());
//    }
//
//    @GetMapping("/all")
//    public ResponseEntity<ResponseApi<List<ProductDoc>>> getAllProducts() {
//        return ResponseEntity.ok(ResponseApi.success("상품 조회 성공", productService.getAllProducts()));
//    }

//    @GetMapping("/elastic")
//    public ResponseEntity<ResponseApi<List<ProductDoc>>> searchByKeyword(@Param("keyword") String keyword) {
//        return ResponseEntity.ok(ResponseApi.success("상품 조회 성공", productService.searchByKeyword(keyword)));
//    }


    @PostMapping
    public ResponseEntity<ResponseApi<String>> registerProduct(@RequestPart("product") ProductDto productDto,
                                                               @RequestPart("file") MultipartFile file) {
        log.info("productDto = {}",productDto);
        log.info("file = {}",file);
        productDto.setImageUrl(s3Service.uploadFile(file));
        productService.registerProduct(productDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseApi.success("상품 등록 성공", "success"));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductDto>> searchProductList(@Param("keyword") String keyword,
                                                              @Param("page") int page,
                                                              @Param("size") int size) {
        return ResponseEntity.ok(productService.searchProductList(keyword,page,size));
    }



}
