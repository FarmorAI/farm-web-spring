package com.farmorai.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiResultDto {
    private String aiResultId;
    private double rateS;
    private double rateA;
    private double rateB;
    private String imageUrl;
    private Long memberId;
    private String createdAt;
}
