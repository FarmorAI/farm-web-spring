package com.farmorai.backend.controller;

import com.farmorai.backend.dto.MemberDto;
import com.farmorai.backend.securityFilter.jwt.JwtTokenProvider;
import com.farmorai.backend.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@Log4j2
@RequiredArgsConstructor
public class SocialController {

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    //소셜 로그인 카카오
    @PostMapping("/api/member/social/kakao")
    public Map<String,Object> getMemberFromKakao(@RequestHeader(value = "Authorization", required = false) String authorizationHeader){
        log.info("authorizationHeader =========================== {} ",authorizationHeader);
        if(authorizationHeader == null){
            return Map.of("result", "fail");
        }
        // Bearer 접두사 제거
        if (!authorizationHeader.startsWith("Bearer ")) {
            log.error("Authorization 헤더가 올바르지 않습니다 {}", authorizationHeader);
            return Map.of("result", "fail");
        }

        String accessToken = authorizationHeader.replace("Bearer ", "").trim();
        log.info(accessToken); // JWT가 아니라 카카오 AccessToken임
        Map<String, Object> kakaoMap = new HashMap<>();
        MemberDto memberDto = memberService.getKakaoMember(accessToken);
        kakaoMap.put("member_id",memberDto.getMemberId());
        kakaoMap.put("email",memberDto.getEmail());
        kakaoMap.put("nickname",memberDto.getNickname());
        kakaoMap.put("role",memberDto.getMemberRole().name());
        kakaoMap.put("social",memberDto.isSocial());
        kakaoMap.put("accessToken", jwtTokenProvider.createJwtToken(memberDto.getEmail(), memberDto.getMemberRole().name(), memberDto.getNickname()));
        log.info("memberDto {} ",memberDto);

        return kakaoMap;
    }




}
