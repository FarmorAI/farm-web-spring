package com.farmorai.backend.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDto {
    private Long cartItemId;
    private Long cartId;
    private Long productId;
    private int quantity;
    private String createdAt;
}
