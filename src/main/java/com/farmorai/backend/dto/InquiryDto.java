package com.farmorai.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InquiryDto {
    private Long inquiryId;
    private String title;
    private String content;
    private InquiryCategory category;
    private String createdAt;
    private String updatedAt;
    private String writer;
}
