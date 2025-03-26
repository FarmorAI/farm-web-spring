package com.farmorai.backend.service;

import com.farmorai.backend.dto.PageRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // 웹 환경 설정 추가
@Slf4j
class NoticeServiceTest {


    @Autowired
    private TestRestTemplate restTemplate;

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