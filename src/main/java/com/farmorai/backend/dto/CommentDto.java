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
    private String content;
    private String createdAt;
    private String updatedAt;
    private Long noticeId;
    private Long memberId;


}
