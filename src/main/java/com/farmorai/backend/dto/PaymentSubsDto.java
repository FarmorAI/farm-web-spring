package com.farmorai.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PaymentSubsDto {
    private Long paymentId;
    private Integer amount;
    private String token;
    private String createdAt;
    private String paymentMethod;
    private String status;
    private String planName;
    private int daysDiff;
}
