package com.farmorai.backend.service;

import com.farmorai.backend.dto.MemberDto;
import com.farmorai.backend.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberDetailsService implements UserDetailsService {
    private final MemberService memberService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Member 객체 조회 및 null 체크
        MemberDto memberDto = memberService.getMemberByEmail(email);
        if(memberDto == null) {
            throw new UsernameNotFoundException("User not found: " + email);
        }

        // Member 인증을 위한 User 객체 생성
        return User.withUsername(memberDto.getEmail())       // Email
                .password(memberDto.getPassword())           // Hashed Password
                .roles(memberDto.getMemberRole().toString()) // Role
                .build();
    }
}
