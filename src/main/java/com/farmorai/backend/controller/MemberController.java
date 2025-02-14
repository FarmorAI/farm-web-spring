package com.farmorai.backend.controller;

import com.farmorai.backend.dto.MemberDto;
import com.farmorai.backend.dto.MemberRole;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.farmorai.backend.service.MemberService;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Controller
@RequestMapping(value = "/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    // 전체 회원 조회
    @ResponseBody
    @GetMapping("/admin")
    public List<MemberDto> getAllMember() {
        return memberService.getAllMember();
    }

    // 회원 등록
    @ResponseBody
    @PostMapping
    public String addMember(@RequestBody MemberDto memberDto) {
        memberDto.setMemberRole(MemberRole.USER);
        memberService.insertMember(memberDto);
        return "success";
    }

    // 회원 조회
    @ResponseBody
    @GetMapping(value = "/auth/{memberId}")
    public MemberDto getMemberById(@PathVariable(value = "memberId") Long memberId) {
        System.out.println(memberId);
        return memberService.getMemberById(memberId);
    }

    // 회원 수정
    @ResponseBody
    @PutMapping(value = "/auth/{memberId}")
    public String updateMember(
            @PathVariable(value = "memberId") Long memberId,
            @RequestBody MemberDto memberDto
    ) {
        memberDto.setMemberId(memberId);
        memberService.updateMember(memberDto);
        return "success";
    }

    // 회원 삭제
    @ResponseBody
    @DeleteMapping(value = "/auth/{memberId}")
    public String deleteMember(
            @PathVariable(value = "memberId") Long memberId
    ) {
        memberService.deleteMember(memberId);
        return "success";
    }
}
