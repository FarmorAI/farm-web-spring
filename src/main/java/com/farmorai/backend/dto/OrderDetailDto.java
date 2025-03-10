package com.farmorai.backend.dto;

import java.math.BigDecimal;

public class OrderDetailDto {
    private Long orderDetailId;
    private Long orderId;
    private Long productId;
    private int quantity;
    private String pname;
    private String imageUrl;
    private BigDecimal price;
    private BigDecimal totalAmount;
    private String createdAt;
    private OrderStatus status;
}
