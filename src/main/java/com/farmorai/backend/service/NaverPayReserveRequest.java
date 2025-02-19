package com.farmorai.backend.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NaverPayReserveRequest {
    private String merchantPayKey;
    private String productName;
    private int productCount;
    private int totalPayAmount;
    private int taxScopeAmount;
    private int taxExScopeAmount;
    private String returnUrl;
}
