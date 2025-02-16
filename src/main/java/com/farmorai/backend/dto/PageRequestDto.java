package com.farmorai.backend.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageRequestDto {

    @Builder.Default
    private int page = 1; // 기본값 1

    @Builder.Default
    private int size = 10; // 기본값 10

    private String title;
    private String content;

    public int getOffset() {
        return (page - 1) * size; // 몇 번째 데이터부터 가져올지 OFFSET을 구하는 메서드
    }

}
