package com.farmorai.backend.controller;

import com.farmorai.backend.dto.MemberDto;

import com.farmorai.backend.securityFilter.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.farmorai.backend.service.MemberService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    // 전체 회원 조회 (관리자 전용)
    @GetMapping(value = "/admin")
    public ResponseEntity<List<MemberDto>> getAllMember() {
        List<MemberDto> members = memberService.getAllMember();
        return ResponseEntity.ok(members);
    }

    // 회원 등록
    @PostMapping(value = "/join")
    public ResponseEntity<String> joinMember(@RequestBody MemberDto memberDto) {
        memberService.insertMember(memberDto);
        return ResponseEntity.status(201).body("success");
    }

    // 닉네임 중복 검사
    @GetMapping(value = "/join/nickname/{nickname}")
    public ResponseEntity<Boolean> checkNickname(@PathVariable String nickname) {
        boolean isAvailable = memberService.checkNickname(nickname);
        return ResponseEntity.ok(isAvailable);
    }

    // 이메일 중복 검사
    @GetMapping(value = "/join/email/{email}")
    public ResponseEntity<Boolean> checkEmail(@PathVariable String email) {
        boolean isAvailable = memberService.checkEmail(email);
        return ResponseEntity.ok(isAvailable);
    }

    // 회원 조회 (by token)
    @GetMapping("/auth")
    public ResponseEntity<MemberDto> getMemberByToken(HttpServletRequest req) {
        // 토큰 추출
        String authValue = req.getHeader("Authorization");
        if (authValue == null || !authValue.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().build();
        }
        // 토큰 검증
        String token = authValue.split(" ")[1];
        if (jwtTokenProvider.isJwtExpired(token)) {
            return ResponseEntity.status(401).build();
        }
        // 토큰에서 회원 정보 추출
        String email = jwtTokenProvider.getEmail(token);
        MemberDto member = memberService.getMemberByEmail(email);

        return Optional.ofNullable(member)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 회원 조회 (by id)
    @GetMapping(value = "/auth/{memberId}")
    public ResponseEntity<MemberDto> getMemberById(@PathVariable Long memberId) {
        MemberDto member = memberService.getMemberById(memberId);
        return Optional.ofNullable(member)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 회원 수정
    @PutMapping(value = "/auth/{memberId}")
    public ResponseEntity<String> updateMember(
            @PathVariable(value = "memberId") Long memberId,
            @RequestBody Map<String, String> updateInfo
    ) {
        MemberDto memberDto = memberService.getMemberById(memberId);
        if (memberDto == null) {
            return ResponseEntity.notFound().build();
        }
        memberDto.setName(updateInfo.get("name"));
        memberDto.setNickname(updateInfo.get("nickname"));
        memberDto.setPhone(updateInfo.get("phone"));
        memberDto.setBirthDate(updateInfo.get("birthDate"));
        memberDto.setAddress(updateInfo.get("address"));

        memberService.updateMember(memberDto);
        return ResponseEntity.ok("success");
    }

    // 회원 삭제
    @DeleteMapping(value = "/auth/{memberId}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long memberId) {
        memberService.deleteMember(memberId);
        return ResponseEntity.noContent().build();
    }

    //JWT 토큰을 이용해서 사용자 정보 반환
    @GetMapping("/user")
    public ResponseEntity<MemberDto> getUserInfo(HttpServletRequest request) {
        // Authorization 헤더에서 JWT 토큰 추출
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().build();
        }

        // "Bearer " 제거 후 토큰 추출
        String token = authorizationHeader.substring(7);
        if (jwtTokenProvider.isJwtExpired(token)) {
            return ResponseEntity.status(401).build();
        }

        // JWT에서 이메일 추출
        String email = jwtTokenProvider.getEmail(token);
        MemberDto member = memberService.getMemberByEmail(email);

        return ResponseEntity.ok(member);
    }


}