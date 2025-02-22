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

    public List<CommentDto> getCommentList(Long boardId) {
        return commentMapper.getCommentList(boardId);
    }

    @Transactional
    public void insertComment(CommentDto commentDto) {
        commentMapper.insertComment(commentDto);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        commentMapper.deleteComment(commentId);
    }

    @Transactional
    public void updateComment(Long commentId, CommentDto commentDto) {
        commentMapper.updateComment(commentId, commentDto);
    }
}
