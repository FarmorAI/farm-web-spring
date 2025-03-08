package com.farmorai.backend.domain;

import lombok.Data;
import lombok.Getter;

@Getter
public class Cart {
    private Long cartId;
    private Long memberId;
    private String createdAt;
}
