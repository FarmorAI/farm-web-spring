package com.farmorai.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SubsDto {
    private Long subsId;
    private SubsStatus status;
    private String startDate;
    private String endDate;
    private String createAt;
    private String canceledAt;
    private Long memberId;
    private Long planId;
}
