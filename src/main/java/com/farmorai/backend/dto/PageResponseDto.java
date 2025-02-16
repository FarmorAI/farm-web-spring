package com.farmorai.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Data
public class PageResponseDto<E>{ // 페이징 결과 DTO

    private List<E> dtoList; // 조회된 데이터 리스트
    private List<Integer> pageNumList; // 페이지 번호 리스트
    private PageRequestDto pageRequestDto; // 페이지 요청 DTO

    private boolean prev, next; // 이전, 다음 페이지 존재 여부

    private int totalCount, prevPage, nextPage, totalPage, currentPage;

    @Builder
    public PageResponseDto(List<E> dtoList, List<Integer> pageNumList, PageRequestDto pageRequestDto, long total) {
        this.dtoList = dtoList;
        this.pageRequestDto = pageRequestDto;
        this.totalCount = (int) total;

        // 현재 페이지
        this.currentPage = pageRequestDto.getPage();

        int end = (int) (Math.ceil(currentPage / 10.0) * 10);
        int start = end - 9;

        // 전체 페이지 수
        int last = (int) (Math.ceil(total / (double) pageRequestDto.getSize()));
        end = Math.min(last, end);

        this.prev = start > 1; // 이전 페이지 존재 여부
        this.next = end < last; // 다음 페이지 존재 여부

        this.pageNumList = IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());

        this.prevPage = prev? start - 1 : 1;
        this.nextPage = next? end + 1 : 0;
        this.totalPage = last;




    }




}
