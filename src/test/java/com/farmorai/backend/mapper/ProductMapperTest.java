package com.farmorai.backend.mapper;

import com.farmorai.backend.dto.ProductDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ProductMapperTest {

    @Autowired
    private ProductMapper productMapper;

    private List<ProductDto> productDtos;

    @BeforeEach
    void setUp(){
        productDtos = new ArrayList<>();
        for (int i = 1; i <= 1000; i++) {
            productDtos.add(new ProductDto("사과"+i, "애플망고", 15000,100,"사과입니다."));
        }

    }

    @Test
    @Rollback(value = false)
    void 상품_대량_등록() throws Exception {

        productMapper.batchRegisterProduct(productDtos);



    }


}