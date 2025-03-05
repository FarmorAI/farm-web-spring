package com.farmorai.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppleDto {
    private Long regionId;
    private String region;
    private Long areaHa;
    private Long productionTon;
}
