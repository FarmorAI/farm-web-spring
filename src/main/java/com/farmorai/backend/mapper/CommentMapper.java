package com.farmorai.backend.mapper;

import com.farmorai.backend.dto.CommentDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper {

    // 댓글 목록 조회
    List<CommentDto> getCommentList(@Param("boardId") Long boardId);

    // 특정 댓글 조회
    CommentDto getCommentById(@Param("commentId") Long commentId);

    // 댓글 삽입
    void insertComment(CommentDto commentDto);

    // 댓글 수정
    void updateComment(@Param("commentId") Long commentId, @Param("commentDto") CommentDto commentDto);

    // 댓글 삭제 (대댓글이 없을 경우만)
    void deleteComment(@Param("commentId") Long commentId);

    // 새 댓글이 원 댓글인 경우 ref 값 업데이트
    void updateCommentRef(@Param("commentId") Long commentId);

    // 대댓글 존재 여부 확인
    boolean hasChildComments(@Param("commentId") Long commentId);

    // 댓글 내용 삭제 (소프트 삭제)
    void markCommentAsDeleted(@Param("commentId") Long commentId);

    // 특정 ref 그룹 내에서 가장 깊은 depth 조회
    int getMaxDepthByRef(@Param("ref") Long ref);
}
