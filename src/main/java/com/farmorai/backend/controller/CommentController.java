package com.farmorai.backend.controller;

import com.farmorai.backend.dto.CommentDto;
import com.farmorai.backend.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/notice/{noticeId}/comment")
    public List<CommentDto> getCommentList(@PathVariable Long noticeId) {
        return commentService.getCommentList(noticeId);
    }

    @PostMapping("/notice/{noticeId}/comment")
    public Map<String,String> insertComment(@PathVariable Long noticeId, @RequestBody CommentDto commentDto) {
        commentService.insertComment(noticeId, commentDto);
        return Map.of("result","success");
    }





}
