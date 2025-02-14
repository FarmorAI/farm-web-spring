package com.farmorai.backend.service;

import com.farmorai.backend.dto.MemberDto;
import com.farmorai.backend.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberMapper memberMapper;

    // 전체 회원 조회
    public List<MemberDto> getAllMember() {
        return memberMapper.getAllMember();
    }

    // 회원 조회 (ID)
    public MemberDto getMemberById(Long memberId) {
        return memberMapper.getMemberById(memberId);
    }

    // 회원 조회 (Email)
    public MemberDto getMemberByEmail(String email) {
        return memberMapper.getMemberByEmail(email);
    }

    // 회원 등록
    public void insertMember(MemberDto memberDto) {
        memberMapper.insertMember(memberDto);
    }

    // 회원 수정
    public void updateMember(MemberDto memberDto) {
        memberMapper.updateMember(memberDto);
    }

    // 회원 삭제
    public void deleteMember(Long memberId) {
        memberMapper.deleteMember(memberId);
    }
}
