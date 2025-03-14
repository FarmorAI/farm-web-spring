package com.farmorai.backend.mapper;

import com.farmorai.backend.dto.NoticeDto;
import com.farmorai.backend.dto.PageRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@Transactional
class NoticeMapperTest {

    @Autowired
    private NoticeMapper noticeMapper;

    private List<NoticeDto> testNotices;
    
    @BeforeEach
    void setUp() {
        testNotices = new ArrayList<>();
        for (int i = 1; i <= 1000; i++) {
            testNotices.add(new NoticeDto("김도훈 잠만보"+i , "김도훈 잠만보"+i));
        }
    }

    @Test
    @Rollback(value = false)
    void insertNotice() {
        NoticeDto newNotice = new NoticeDto(null, "테스트제목1", "테스트내용1", "작성자", 0, null,null);
        noticeMapper.insertNotice(33L, newNotice);
    }

    @Test
    void getNoticeById(){
        NoticeDto notice = noticeMapper.getNoticeDetail(22L);
        assertNotNull(notice);
    }

    @Test
    void testSingleInsertPerformance(){
        long startTime = System.currentTimeMillis();
        for (NoticeDto testNotice : testNotices) {
            noticeMapper.insertNotice(33L, testNotice);
        }
        long endTime = System.currentTimeMillis();
        log.info("single insert 총 소요시간 : " + (endTime - startTime) + "ms");
    }

    @Test
    void testBatchInsertPerformance(){
        long startTime = System.currentTimeMillis();
        noticeMapper.insertBatchNotice(33L, testNotices);
        long endTime = System.currentTimeMillis();
        log.info("batch insert 총 소요시간 : " + (endTime-startTime) + "ms");
    }

    @Test
    void testGetNoticeList(){
        long startTime = System.currentTimeMillis();
        List<NoticeDto> noticeList = noticeMapper.getNoticeList(new PageRequestDto(1,1000,"",""));
        long endTime = System.currentTimeMillis();
        log.info("noticeList.size() = {} " ,noticeList.size());
        log.info("총 소요시간 : " + (endTime-startTime) + "ms");
    }

    @Test
    void testSearchNotice(){
        long startTime = System.currentTimeMillis();
        String title = "김도훈";
        noticeMapper.getNoticeList(new PageRequestDto(1, noticeMapper.getNoticeListCount(new PageRequestDto(1, 1000, title, "")),title,""));
        long endTime = System.currentTimeMillis();
        log.info("총 소요시간 : " + (endTime-startTime) + "ms");
    }



}