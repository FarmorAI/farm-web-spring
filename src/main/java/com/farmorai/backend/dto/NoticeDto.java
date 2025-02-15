package com.farmorai.backend.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoticeDto {

    private Long notice_id;

    private String title;

    private String content;

    private String writer;

    private int views;

    private String created_at;

    private String updated_at;


}
