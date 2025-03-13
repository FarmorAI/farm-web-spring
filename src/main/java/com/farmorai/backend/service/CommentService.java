package com.farmorai.backend.service;

import com.farmorai.backend.dto.CommentDto;
import com.farmorai.backend.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {
    private final CommentMapper commentMapper;

    // 댓글 리스트 조회
    public List<CommentDto> getCommentList(Long boardId) {
        return commentMapper.getCommentList(boardId);
    }

    // 댓글 추가
    @Transactional
    public void insertComment(CommentDto commentDto) {
        if (commentDto.getParentId() == null) { // 원댓글이면 ref가 null
            // 원댓글 기본 값 설정
            commentDto.setDepth(0);
            commentDto.setLevel(0);
            commentDto.setRef(0L);
            commentMapper.insertComment(commentDto);
            // 삽입 후 자기 자신을 ref로 설정
            commentDto.setRef(commentDto.getCommentId());
            commentMapper.updateCommentRef(commentDto.getCommentId());
        } else { // 대댓글
            CommentDto parent = commentMapper.getCommentById(commentDto.getParentId());
            if (parent == null) {
                throw new IllegalArgumentException("부모 댓글이 존재하지 않습니다. parentId 값: " + commentDto.getParentId());
            }
            // 같은 ref 내에서 가장 큰 depth 값을 찾고, 그 값 + 1로 설정
            int maxDepth = commentMapper.getMaxDepthByRef(parent.getRef());
            commentDto.setDepth(maxDepth + 1);
            // 부모댓글의 level 값에 +1로 설정
            commentDto.setLevel(parent.getLevel() + 1);

            commentDto.setRef(parent.getRef());

            commentMapper.insertComment(commentDto);
        }
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long commentId) {
        if (commentMapper.getCommentById(commentId) == null) {
            throw new IllegalArgumentException("존재하지 않는 댓글입니다.");
        }

        if (commentMapper.hasChildComments(commentId)) {
            commentMapper.markCommentAsDeleted(commentId); // 내용만 변경 (소프트 삭제)
        } else {
            commentMapper.deleteComment(commentId); // 실제 삭제
        }
    }

    // 댓글 수정
    @Transactional
    public void updateComment(Long commentId, CommentDto commentDto) {
        CommentDto existingComment = commentMapper.getCommentById(commentId);
        if (existingComment == null) {
            throw new IllegalArgumentException("존재하지 않는 댓글입니다.");
        }

        commentMapper.updateComment(commentId, commentDto);
    }
}
