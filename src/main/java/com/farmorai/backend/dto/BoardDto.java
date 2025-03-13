package com.farmorai.backend.dto;

import lombok.Data;

@Data
public class BoardDto {
    private Long boardId;
    private String title;
    private String content;
    private String writer;
    private int views;
    private String createdAt;
    private String updatedAt;
}
