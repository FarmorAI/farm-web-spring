package com.farmorai.backend.dto;

import lombok.Data;

@Data
public class BoardDto {
    private Long board_id;
    private String title;
    private String content;
    private int views;
    private String created_at;
    private String updated_at;
}
