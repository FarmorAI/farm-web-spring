package com.farmorai.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyPageDto {
    private Long memberId;

    // 회원정보
    private String email;
    private String nickname;
    private String phone;
    private String address;
    private String memberCreatedAt;

    // 구독정보
    private Integer planId;
    private String startDate;
    private String endDate;

    // 내 게시글
    private String boardTitles;
    private String boardCreatedDates;
    private List<Map<String, Object>> boardInfo;

    // 문의내역
    private String inquiryTitles;
    private String inquiryCreatedDates;
    private List<Map<String, Object>> inquiryInfo;

}
