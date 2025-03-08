package com.farmorai.backend.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class CartItem {
    private Long cartItemId;
    private Long cartId;
    private Long productId;
    private int quantity;
    private String createdAt;

}
