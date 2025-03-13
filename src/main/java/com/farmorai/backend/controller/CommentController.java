package com.farmorai.backend.controller;

import com.farmorai.backend.dto.CommentDto;
import com.farmorai.backend.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board/comments")
public class CommentController {
    private final CommentService commentService;

    // 댓글 목록 조회
    @GetMapping("/{boardId}")
    public ResponseEntity<List<CommentDto>> getCommentList(@PathVariable Long boardId) {
        List<CommentDto> comments = commentService.getCommentList(boardId);
        return ResponseEntity.ok(comments);
    }

    // 댓글 추가
    @PostMapping
    public ResponseEntity<Map<String, String>> insertComment(@RequestBody CommentDto commentDto) {
        Map<String, String> response = new HashMap<>();
        try {
            commentService.insertComment(commentDto);
            response.put("result", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("result", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Map<String, String>> deleteComment(@PathVariable Long commentId) {
        Map<String, String> response = new HashMap<>();
        try {
            commentService.deleteComment(commentId);
            response.put("result", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("result", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 댓글 수정
    @PutMapping("/{commentId}")
    public ResponseEntity<Map<String, String>> updateComment(@PathVariable Long commentId, @RequestBody CommentDto commentDto) {
        Map<String, String> response = new HashMap<>();
        try {
            commentService.updateComment(commentId, commentDto);
            response.put("result", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("result", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
