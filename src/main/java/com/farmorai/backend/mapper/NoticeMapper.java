package com.farmorai.backend.mapper;

import com.farmorai.backend.dto.NoticeDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NoticeMapper {

    List<NoticeDto> getNoticeList();


    void insertNotice(NoticeDto noticeDto);

    void deleteNotice(Long notice_no);

    
}
