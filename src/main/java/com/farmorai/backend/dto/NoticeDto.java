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

    private Long notice_no;

    private String title;

    private String content;

    private String writer;

    private String created_at;

    private String modified_at;


}
