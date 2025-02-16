package com.farmorai.backend.service;

import com.farmorai.backend.dto.CommentDto;
import com.farmorai.backend.mapper.CommentMapper;
import com.farmorai.backend.mapper.NoticeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentMapper commentMapper;

    public List<CommentDto> getCommentList(Long noticeId) {
        return commentMapper.getCommentList(noticeId);
    }

    public void insertComment(Long noticeId, CommentDto commentDto) {
        commentMapper.insertComment(noticeId, commentDto);
    }
}
