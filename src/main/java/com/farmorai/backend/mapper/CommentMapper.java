package com.farmorai.backend.mapper;

import com.farmorai.backend.dto.CommentDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper {


    List<CommentDto> getCommentList(Long noticeId);


    void insertComment(@Param("noticeId") Long noticeId, @Param("comment") CommentDto commentDto);
}
