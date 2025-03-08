package com.farmorai.backend.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDto {
    private Long cartItemId;
    private Long cartId;
    private Long productId;
    private int quantity;
    private String createdAt;
    private BigDecimal price;
    private String pname;
    private String imageUrl;
}
