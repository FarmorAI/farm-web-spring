package com.farmorai.backend.dto;

import lombok.Data;

@Data
public class CommentDto {
    private Long comment_id;
    private Long board_id;
    private String content;
    private String created_at;
    private String updated_at;
    private Long parent_comment_id;
}
