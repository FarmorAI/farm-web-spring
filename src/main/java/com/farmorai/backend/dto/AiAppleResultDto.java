package com.farmorai.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiAppleResultDto {
    private Long appleResultId;
    private Long aiResultId;
    private double ripeness;
    private double redRatio;
    private double greenRatio;
    private double brownRatio;
    private String grade;
    private String createdAt;
}
