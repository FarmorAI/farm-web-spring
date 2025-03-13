package com.farmorai.backend.domain;

import com.farmorai.backend.dto.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class Order {
    private Long ordersId;
    private Long memberId;
    private String orderNumber;
    private BigDecimal totalAmount;
    private BigDecimal shippingFee;
    private OrderStatus status;

}
