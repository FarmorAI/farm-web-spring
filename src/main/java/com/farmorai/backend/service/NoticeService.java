package com.farmorai.backend.service;


import com.farmorai.backend.dto.NoticeDto;
import com.farmorai.backend.dto.PageRequestDto;
import com.farmorai.backend.dto.PageResponseDto;
import com.farmorai.backend.mapper.NoticeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {

    private final NoticeMapper noticeMapper;


    public PageResponseDto<NoticeDto> getNoticeList(PageRequestDto pageRequestDto) {
        List<NoticeDto> noticeList = noticeMapper.getNoticeList(pageRequestDto);
        int totalCount = noticeMapper.getNoticeListCount(pageRequestDto);
        return PageResponseDto.<NoticeDto>builder()
                .dtoList(noticeList)
                .pageRequestDto(pageRequestDto)
                .total(totalCount)
                .build();
    }

    @Transactional
    public NoticeDto getNoticeDetail(Long noticeId) {
        noticeMapper.updateNoticeViews(noticeId); // 조회수 증가
        return noticeMapper.getNoticeDetail(noticeId); // 상세 조회
    }


    @Transactional
    public void insertNotice(NoticeDto noticeDto) {
        noticeMapper.insertNotice(noticeDto);
    }

    @Transactional
    public void deleteNotice(Long noticeId) {
        noticeMapper.deleteNotice(noticeId);
    }


    @Transactional
    public void updateNotice(Long noticeId, NoticeDto noticeDto) {
        noticeMapper.updateNotice(noticeId, noticeDto);
    }


}
