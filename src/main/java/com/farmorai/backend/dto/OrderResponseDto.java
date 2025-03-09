package com.farmorai.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class OrderResponseDto {
    private Long orderId;
    private BigDecimal totalAmount;
    private BigDecimal shippingFee;
    private OrderStatus status;
    private List<OrderItemDto> orderItems;
}
