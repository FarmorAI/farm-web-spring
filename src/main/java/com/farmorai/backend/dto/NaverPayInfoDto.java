package com.farmorai.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NaverPayInfoDto {
    private String merchantPayKey;
    private String productName;
    private int productCount;
    private int totalPayAmount;
    private int taxScopeAmount;
    private int taxExScopeAmount;
    private String returnUrl;
}
