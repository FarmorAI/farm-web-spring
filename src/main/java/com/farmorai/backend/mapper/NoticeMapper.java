package com.farmorai.backend.mapper;

import com.farmorai.backend.dto.NoticeDto;
import com.farmorai.backend.dto.PageRequestDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NoticeMapper {

    List<NoticeDto> getNoticeList(PageRequestDto pageRequestDto);
    int getNoticeListCount(PageRequestDto pageRequestDto);

    NoticeDto getNoticeDetail(Long noticeId);
    void updateNoticeViews(Long noticeId);


    void insertNotice(NoticeDto noticeDto);

    void deleteNotice(Long noticeId);


    void updateNotice(@Param("noticeId") Long noticeId, @Param("notice") NoticeDto noticeDto);


}
