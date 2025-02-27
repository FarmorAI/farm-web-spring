package com.farmorai.backend.controller;

import com.farmorai.backend.dto.MemberDto;
import com.farmorai.backend.securityFilter.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.farmorai.backend.service.MemberService;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    // 전체 회원 조회
    @GetMapping(value = "/admin/members")
    public List<MemberDto> getAllMember() {
        return memberService.getAllMember();
    }

    // 회원 등록
    @PostMapping(value = "/join")
    public String addMember(@RequestBody MemberDto memberDto) {
        memberService.insertMember(memberDto);
        return "success";
    }

    // 닉네임 중복 검사
    @GetMapping(value = "/join/nickname/{nickname}")
    public boolean checkJoin(@PathVariable(value = "nickname") String nickname) {
        return memberService.checkNickname(nickname);
    }

    // 이메일 중복 검사
    @GetMapping(value = "/join/email/{email}")
    public boolean checkEmail(@PathVariable(value = "email") String email) {
        return memberService.checkEmail(email);
    }


    // 회원 조회 (by id)
    @GetMapping(value = "/auth/id/{memberId}")
    public MemberDto getMemberById(@PathVariable(value = "memberId") Long memberId) {
        return memberService.getMemberById(memberId);
    }

    // 회원 조회 (by email)
    @GetMapping(value = "/auth/email/{email}")
    public MemberDto getMemberByEmail(@PathVariable(value = "email") String email) {
        return memberService.getMemberByEmail(email);
    }

    // 회원 수정
    @PutMapping(value = "/auth/{memberId}")
    public String updateMember(
            @PathVariable(value = "memberId") Long memberId,
            @RequestBody Map<String, String> updateInfo
    ) {
        MemberDto memberDto = memberService.getMemberById(memberId);
        memberDto.setName(updateInfo.get("name"));
        memberDto.setNickname(updateInfo.get("nickname"));
        memberDto.setPhone(updateInfo.get("phone"));
        memberDto.setBirthDate(updateInfo.get("birthDate"));
        memberDto.setAddress(updateInfo.get("address"));
        memberService.updateMember(memberDto);
        return "success";
    }

    // 회원 삭제
    @DeleteMapping(value = "/auth/{memberId}")
    public String deleteMember(@PathVariable(value = "memberId") Long memberId) {
        memberService.deleteMember(memberId);
        return "success";
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