package com.farmorai.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class MemberDto {
    private Long memberId;
    private String email;
    private String password;
    private String name;
    private String nickname;
    private String phone;
    private String birthDate;
    private MemberRole memberRole;
    private String address;
    private String createdAt;
    private String updatedAt;
}