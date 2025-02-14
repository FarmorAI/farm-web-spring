package com.farmorai.backend.service;

import com.farmorai.backend.domain.Member;
import com.farmorai.backend.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberMapper memberMapper;

    public Member getMemberById(Long id) {
        return memberMapper.getMemberById(id);
    }
}
