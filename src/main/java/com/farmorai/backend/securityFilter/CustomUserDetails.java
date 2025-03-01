package com.farmorai.backend.securityFilter;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class CustomUserDetails extends User {
    private final Long memberId;
    private final String nickname;

    public CustomUserDetails(
            Long memberId,
            String username,
            String password,
            Collection<? extends GrantedAuthority> authorities,
            String nickname
    ) {
        super(username, password, authorities);
        this.memberId = memberId;
        this.nickname = nickname;
    }
}
