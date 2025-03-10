package com.farmorai.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDto {
    private Long ordersId;
    private BigDecimal totalAmount;
    private BigDecimal shippingFee;
    private OrderStatus status;
    private List<OrderItemDto> orderItems;
    private String orderNumber;
    private String createdAt;
}
