package com.farmorai.backend.mapper;


import com.farmorai.backend.dto.CommentDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper {
    List<CommentDto> getCommentList(Long boardId);

    void insertComment(CommentDto commentDto);

    void deleteComment(Long commentId);

    void updateComment(@Param ("commentId") Long commentId,@Param("comment") CommentDto commentDto);
}
