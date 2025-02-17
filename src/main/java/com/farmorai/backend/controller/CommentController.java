package com.farmorai.backend.controller;

import com.farmorai.backend.dto.CommentDto;
import com.farmorai.backend.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board/comments")
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/{boardId}")
    public List<CommentDto> getCommentList(@PathVariable Long boardId) {
        return commentService.getCommentList(boardId);
    }

    @PostMapping
    public Map<String,String> insertComment(@RequestBody CommentDto commentDto){
        commentService.insertComment(commentDto);
        return Map.of("result","success");
    }

    @DeleteMapping("/{commentId}")
    public Map<String,String> deleteComment(@PathVariable Long commentId){
        commentService.deleteComment(commentId);
        return Map.of("result","success");
    }

    @PutMapping("/{commentId}")
    public Map<String,String> updateComment(@PathVariable Long commentId, @RequestBody CommentDto commentDto){
        commentService.updateComment(commentId, commentDto);
        return Map.of("result","success");
    }

}

