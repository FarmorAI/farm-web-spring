package com.farmorai.backend.controller;

import com.farmorai.backend.dto.CommentDto;
import com.farmorai.backend.securityFilter.CustomUserDetails;
import com.farmorai.backend.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<Map<String, String>> insertComment(
            @RequestBody CommentDto commentDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Map<String, String> response = new HashMap<>();
        try {
            if (userDetails == null) {
                response.put("result", "error");
                response.put("message", "로그인이 필요합니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            commentDto.setMemberId(userDetails.getMemberId());
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
    public ResponseEntity<Map<String, String>> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Map<String, String> response = new HashMap<>();
        try {
            commentService.deleteComment(commentId, userDetails.getMemberId());
            response.put("result", "success");
            return ResponseEntity.ok(response);
        } catch (AccessDeniedException e) {
            response.put("result", "error");
            response.put("message", "본인이 작성한 댓글만 삭제할 수 있습니다.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
    }

    // 댓글 수정
    @PutMapping("/{commentId}")
    public Map<String, String> updateComment(@PathVariable Long commentId, @RequestBody CommentDto commentDto) {
        commentService.updateComment(commentId, commentDto);
        return Map.of("result", "success");
    }
}
