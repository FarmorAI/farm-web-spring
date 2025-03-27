package com.farmorai.backend.service;

import com.farmorai.backend.dto.PageRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // 웹 환경 설정 추가
@Slf4j
class NoticeServiceTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private static final int THREAD_COUNT = 100;

    @Test
    void 공지사항_API_멀티스레드_응답시간_테스트() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        List<Long> responseTimes = new CopyOnWriteArrayList<>();
        for (int i = 0; i < THREAD_COUNT; i++) {
            executorService.execute(() -> {
                try {
                    long start = System.currentTimeMillis();
                    ResponseEntity<String> response = restTemplate.getForEntity("/api/notice/list", String.class);
                    long end = System.currentTimeMillis();
                    responseTimes.add(end - start);
                } catch (Exception e) {
                    log.error("요청 실패", e);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await(); // 모든 스레드 완료 대기
        executorService.shutdown();

        long totalTime = responseTimes.stream().mapToLong(Long::longValue).sum();
        long average = totalTime / responseTimes.size();

        log.info("✅ 총 요청 수: {}", THREAD_COUNT);
        log.info("📊 평균 응답시간: {} ms", average);
        log.info("📉 최소 응답시간: {} ms", responseTimes.stream().min(Long::compare).orElse(0L));
        log.info("📈 최대 응답시간: {} ms", responseTimes.stream().max(Long::compare).orElse(0L));
    }

    @Test
    void 공지사항_API_응답시간_테스트() {
        long averageExecutionTime = 0L;
        for (int i = 0; i < 100; i++) {
            long start = System.currentTimeMillis();
            restTemplate.getForEntity("/api/notice/list", String.class);
            long end = System.currentTimeMillis();
            averageExecutionTime += (end - start);
        }
        log.info("API 응답시간 평균 : {} ms", averageExecutionTime / 100);
    }

}