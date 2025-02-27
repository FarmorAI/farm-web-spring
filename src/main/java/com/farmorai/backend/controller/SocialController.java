package com.farmorai.backend.controller;

import com.farmorai.backend.dto.MemberDto;
import com.farmorai.backend.securityFilter.jwt.JwtTokenProvider;
import com.farmorai.backend.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@RestController
@Log4j2
@RequiredArgsConstructor
public class SocialController {

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${NAVERLOGIN_CLIENT_ID}")
    private String naverClientId;
    @Value("${NAVERLOGIN_CLIENT_SECRET}")
    private String naverClientSecret;

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

    // 소셜 로그인 네이버
    @PostMapping("/api/member/social/naver")
    public Map<String, Object> getMemberFromNaver(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        log.info("authorizationHeader =========================== {} ", authorizationHeader);
        if (authorizationHeader == null) {
            return Map.of("result", "fail");
        }
        // Bearer 접두사 제거
        if (!authorizationHeader.startsWith("Bearer ")) {
            log.error("Authorization 헤더가 올바르지 않습니다 {}", authorizationHeader);
            return Map.of("result", "fail");
        }

        String accessToken = authorizationHeader.replace("Bearer ", "").trim();
        log.info(accessToken); // JWT가 아니라 네이버 AccessToken임
        Map<String, Object> naverMap = new HashMap<>();
        MemberDto memberDto = memberService.getNaverMember(accessToken);
        naverMap.put("member_id", memberDto.getMemberId());
        naverMap.put("email", memberDto.getEmail());
        naverMap.put("nickname", memberDto.getNickname());
        naverMap.put("role", memberDto.getMemberRole().name());
        naverMap.put("social", memberDto.isSocial());
        naverMap.put("accessToken", jwtTokenProvider.createJwtToken(memberDto.getEmail(), memberDto.getMemberRole().name(), memberDto.getNickname()));
        log.info("memberDto {} ", memberDto);

        return naverMap;
    }

    @GetMapping("/api/member/social/naver/token")
    public ResponseEntity<?> getNaverToken(@RequestParam("code") String code) {
        String naverTokenUrl = "https://nid.naver.com/oauth2.0/token";

        UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(naverTokenUrl)
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", naverClientId)
                .queryParam("client_secret", naverClientSecret)
                .queryParam("code", code)
                .build();

        RestTemplate restTemplate = new RestTemplate();
        try {
            Map<String, Object> response = restTemplate.getForObject(uriComponents.toUri(), Map.class);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("토큰 발급 실패", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("토큰 발급 실패");
        }
    }



}
