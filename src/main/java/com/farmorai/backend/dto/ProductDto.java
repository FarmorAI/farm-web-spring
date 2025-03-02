package com.farmorai.backend.dto;

import lombok.Data;

@Data
public class ProductDto {

    private String name;
    private String variety; // 품종
    private int price;
    private int stock;
    private String description;
    private String imageUrl;
    private String category;
    private String createdAt;
    private String updatedAt;


}
