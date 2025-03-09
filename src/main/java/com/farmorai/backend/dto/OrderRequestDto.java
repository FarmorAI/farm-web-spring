package com.farmorai.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDto {
    private List<OrderItemDto> orderItems;
    private BigDecimal totalAmount;
    private BigDecimal shippingFee;
}
