package com.farmorai.backend.dto;

import com.farmorai.backend.mapper.InquiryMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest  // 스프링 컨테이너에서 실제 객체를 가져와서 테스트 가능
class PageResponseDtoTest {

    @Autowired
    private InquiryMapper inquiryMapper;     // 실제 빈 주입

    private List<InquiryDto> dtoList;        // DTO 리스트가 들어감
    private List<Integer> pageNumList;       // 페이지 번호 리스트
    private PageRequestDto pageRequestDto;   // 페이지 요청 DTO
    private boolean prev, next;              // 이전 다음페이지 존재 여부

    private int totalCount, prevPage, nextPage, totalPage, current;

    @BeforeEach
    public void init() {
        this.pageRequestDto = PageRequestDto.builder()
                .page(2)  // 현재페이지
                .size(5)  // 페이지 당 데이터 수
                .build();

        this.current = pageRequestDto.getPage();
        this.dtoList = inquiryMapper.getInquiryList(pageRequestDto);
        this.totalCount = inquiryMapper.getInquiryListCnt();
    }


    @Test
    @DisplayName("PageResponseDto_생성_테스트")
    public void testResponse() {
        /**
         * 한 블록당 10개의 페이지 번호 표시
         * start : 현재 페이지가 속한 블록의 첫 페이지
         * end : 현재 페이지가 속한 블록의 마지막 페이지
         */
        int end = (int) (Math.ceil(current / 10.0) * 10);
        int start = end - 9;

        // 전체 페이지 수 = 전체 데이터 수 / 페이지 당 데이터 사이즈
        int totalPageCnt = (int) (Math.ceil(totalCount / (double) pageRequestDto.getSize()));
        end = Math.min(totalPageCnt, end);  // ?

        this.prev = start > 1;              // 이전 블록 존재 여부
        this.next = end < totalPageCnt;     // 다음 블록 존재 여부

        /**
         * IntStream.rangeClosed(start, end):
         * start부터 end까지의 숫자를 포함한 정수 스트림(IntStream) 생성
         * 예: IntStream.rangeClosed(1, 5) → 1, 2, 3, 4, 5
         *
         * .boxed():
         * 기본형 int 스트림을 객체형 Integer 스트림으로 변환
         * IntStream → Stream<Integer>로 변환
         *
         * .collect(Collectors.toList()):
         * Stream<Integer>를 **리스트(List<Integer>)**로 변환
         */
        this.pageNumList = IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());

        this.prevPage = prev? start-1 : 1;
        this.nextPage = next? end+1 : 0;
        this.totalPage = totalPageCnt;

        log.info("start = {}, end = {}, totalCount = {}", start, end, totalCount);
        log.info("prevBool = {}, nextBool = {}, pageNumList = {}", prev, next, pageNumList);
        log.info("prevPage = {}, nextPage = {}, totalPage = {}", prevPage, nextPage, totalPage);

        assertEquals(pageNumList.size(), totalPage);
    }
}