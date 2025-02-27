package com.farmorai.backend.controller;

import com.farmorai.backend.dto.MemberDto;
import com.farmorai.backend.securityFilter.jwt.JwtTokenProvider;
import com.farmorai.backend.service.MemberService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@Log4j2
@RequiredArgsConstructor
public class SocialController {

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    //소셜 로그인 카카오
    @PostMapping("/api/member/social/kakao")
    public ResponseEntity<Map<String, Object>> getMemberFromKakao(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {

        log.info("authorizationHeader =========================== {} ", authorizationHeader);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            log.error("Authorization 헤더가 올바르지 않습니다! {}", authorizationHeader);
            return ResponseEntity.status(UNAUTHORIZED).body(Map.of("result", "fail"));
        }

        // Bearer 접두사 제거 후 Access Token 추출
        String accessToken = authorizationHeader.replace("Bearer ", "").trim();
        log.info("카카오 Access Token: {}", accessToken);

        // 카카오에서 사용자 정보 가져오기
        MemberDto memberDto = memberService.getKakaoMember(accessToken);



        // 응답 바디에 포함할 데이터
        Map<String, Object> kakaoMap = new HashMap<>();
        kakaoMap.put("member_id", memberDto.getMemberId());
        kakaoMap.put("email", memberDto.getEmail());
        kakaoMap.put("nickname", memberDto.getNickname());
        kakaoMap.put("role", memberDto.getMemberRole().name());
        kakaoMap.put("social", memberDto.isSocial());

        log.info("회원 정보: {}", memberDto);

        // 헤더에 JWT 토큰 생성 및 추가
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwtTokenProvider.createJwtToken(
                memberDto.getEmail(),
                memberDto.getMemberRole().name(),
                memberDto.getNickname()));

        // ResponseEntity 헤더 + 바디 함께 반환
        return ResponseEntity.ok()
                .headers(headers)
                .body(kakaoMap);
    }

    @PostMapping("/api/member/social/google")
    public ResponseEntity<Map<String, Object>> getMemberFromGoogle(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        log.info("Authorization Header =========================== {} ", authorizationHeader);
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            log.error("Authorization 헤더가 올바르지 않습니다! {}", authorizationHeader);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("result", "fail"));
        }

        // Bearer 접두사 제거 후 Access Token 추출
        String accessToken = authorizationHeader.replace("Bearer ", "").trim();
        log.info("Google Access Token: {}", accessToken);


        MemberDto memberDto = memberService.getGoogleMember(accessToken);


        Map<String, Object> googleMap = new HashMap<>();
        googleMap.put("member_id", memberDto.getMemberId());
        googleMap.put("email", memberDto.getEmail());
        googleMap.put("nickname", memberDto.getNickname());
        googleMap.put("role", memberDto.getMemberRole().name());
        googleMap.put("social", memberDto.isSocial());


        // 🔹 헤더에 JWT 토큰 생성 및 추가
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwtTokenProvider.createJwtToken(
                memberDto.getEmail(),
                memberDto.getMemberRole().name(),
                memberDto.getNickname()));

        // 🔹 ResponseEntity로 헤더 + 바디 함께 반환
        return ResponseEntity.ok()
                .headers(headers)
                .body(googleMap);
    }
}



