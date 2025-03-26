package com.farmorai.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

    private Long productId;
    private String name;
    private String variety; // 품종
    private int price;
    private int stock;
    private String description;
    private String imageUrl;
    private String createdAt;
    private String updatedAt;


    public ProductDto(String name, String variety, int price, int stock, String description) {
        this.name = name;
        this.variety = variety;
        this.price = price;
        this.stock = stock;
        this.description = description;
    }


}
