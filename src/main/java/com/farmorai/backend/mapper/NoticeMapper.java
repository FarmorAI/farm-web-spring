package com.farmorai.backend.mapper;

import com.farmorai.backend.dto.NoticeDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NoticeMapper {

    List<NoticeDto> getNoticeList();

    NoticeDto getNoticeDetail(Long noticeId);
    void updateNoticeViews(Long noticeId);


    void insertNotice(NoticeDto noticeDto);

    void deleteNotice(Long noticeId);


    void updateNotice(@Param("noticeId") Long noticeId, @Param("notice") NoticeDto noticeDto);


}
