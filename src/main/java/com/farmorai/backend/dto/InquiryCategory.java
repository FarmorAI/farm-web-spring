package com.farmorai.backend.dto;

public enum InquiryCategory {
    GENERAL("일반 문의"),
    BILLING("결제 문의"),
    TECHNICAL("서비스 이용 문의"),
    OTHER("기타");

    private final String korean;

    InquiryCategory(String korean) {
        this.korean = korean;
    }

    public String getKorean() {
        return korean;
    }

    public static InquiryCategory fromKorean(String korean) {
        for (InquiryCategory category : values()) {
            if (category.korean.equals(korean)) {
                return category;
            }
        }
        throw new IllegalStateException("해당하는 카테고리가 없습니다: " + korean);
    }
}
