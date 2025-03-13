package com.farmorai.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDto {
    private Long orderDetailId;
    private Long ordersId;
    private Long productId;
    private String pname;
    private String imageUrl;
    private BigDecimal price;
    private int quantity;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private String orderNumber;
    private String createdAt;

}
