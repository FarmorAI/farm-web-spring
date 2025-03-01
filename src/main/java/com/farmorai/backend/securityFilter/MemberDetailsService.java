package com.farmorai.backend.securityFilter;

import com.farmorai.backend.dto.MemberDto;
import com.farmorai.backend.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class MemberDetailsService implements UserDetailsService {
    private final MemberMapper memberMapper;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // Member 객체 조회 및 null 체크
        MemberDto memberDto = memberMapper.getMemberByEmail(email);
        if(memberDto == null) {
            throw new UsernameNotFoundException("User not found: " + email);
        }

        // 권한 생성
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(
                "ROLE_" + memberDto.getMemberRole().toString()
        );

        // Member 인증을 위한 User 객체 생성
        return new CustomUserDetails(
                memberDto.getMemberId(),
                memberDto.getEmail(),
                memberDto.getPassword(),
                Collections.singleton(authority),
                memberDto.getNickname()
        );
    }
}
