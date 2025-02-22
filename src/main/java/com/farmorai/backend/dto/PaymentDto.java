package com.farmorai.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PaymentDto {
    private Long paymentId;
    private int amount;
    private String token;
    private String createdAt;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private Long planId;
    private Long memberId;
    private Long subsId;
}