package com.farmorai.backend.controller;

import com.farmorai.backend.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
@RequiredArgsConstructor
public class SocialController {

    private final MemberService memberService;

    //소셜 로그인 카카오
    @PostMapping("/api/member/kakao")
    public String[] getMemberFromKakao(@RequestHeader(value = "Authorization", required = false) String authorizationHeader){
        log.info("authorizationHeader =========================== {} ",authorizationHeader);
        if(authorizationHeader == null){
            return new String[]{"fail"};
        }
        // Bearer 접두사 제거
        if (!authorizationHeader.startsWith("Bearer ")) {
            log.error("🚨 Authorization 헤더가 올바르지 않습니다! {}", authorizationHeader);
            return new String[]{"fail"};
        }

        log.info("Authorization {} ",authorizationHeader);
        String accessToken = authorizationHeader.replace("Bearer ", "").trim();
        log.info(accessToken);
        memberService.getKakaoMember(accessToken);
        return new String[]{"success"};
    }




}
