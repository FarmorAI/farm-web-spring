package com.farmorai.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private Long commentId;
    private Long boardId;
    private Long memberId;
    private String content;
    private String createdAt;
    private String updatedAt;

    private Long ref;      // 댓글 그룹 (원댓글이면 자기 ID, 대댓글이면 부모 ref)
    private int depth;     // 댓글 계층 (원댓글: 0, 대댓글: 1, ...)
    private int level;

    private Long parentId;
}
