package com.farmorai.backend.service;

import com.farmorai.backend.dto.ProductDto;
import com.farmorai.backend.mapper.ProductMapper;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@Transactional
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductMapper productMapper;


    @Test
    void 재고_감소_정상로직() throws Exception {
        //given
        Long productId = 1L;
        int initialStock = 50;
        int quantity = 5;

        //when
        productService.decreaseStock(productId,quantity);

        //then
        Optional<ProductDto> product = productMapper.getProductById(productId);
        int stock = product.get().getStock();
        assertEquals(initialStock - quantity, stock);

    }

    @Test
    void 재고_부족_예외발생() throws Exception {
        //given
        Long productId = 1L;
        int initialStock = 50; //현재 재고가 50개인 상황
        int quantity = 55;

        //when & then
        Assertions.assertThatThrownBy(() -> productService.decreaseStock(productId, quantity))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("재고가 부족합니다.");

    }

    @Test
    void 재고_감소_예외발생() throws Exception {
        //given
        Long productId = 1L;
        int quantity = -5;

        //when & then
        Assertions.assertThatThrownBy(() -> productService.decreaseStock(productId,quantity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("감소할 수량은 0보다 커야 합니다.");

    }

    @Test
    void 동시성_테스트_10명_동시_상품구매() throws InterruptedException {
        //given
        Long productId = 1L;
        int initialStock = 500;
        int purchaseQuantity = 10;
        int threadCount = 10;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        //when 10명의 사용자가 동시에 상품을 구매하는 시나리오
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    productService.decreaseStock(productId, purchaseQuantity);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                    log.error(e.getMessage());
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await(); // 모든 쓰레드가 종료될 때까지 대기
        executorService.shutdown();

        //then
        Optional<ProductDto> product = productMapper.getProductById(productId);
        log.info("최종 재고량 = {}", product.get().getStock());
        log.info("성공한 요청 수 = {}", successCount.get());
        log.info("실패한 요청 수 = {}", failCount.get());




    }





}