package com.farmorai.backend.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NaverPayReserveResponse {
    private String resultCode;
    private String resultMessage;
    private String reserveId;
    private String paymentId;
    private String orderUrl;
}
