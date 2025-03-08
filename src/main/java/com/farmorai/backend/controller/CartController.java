package com.farmorai.backend.controller;

import com.farmorai.backend.dto.response.ApiResponse;
import com.farmorai.backend.securityFilter.CustomUserDetails;
import com.farmorai.backend.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> addCart(Long memberId, Long productId, int quantity) {
        cartService.addCart(memberId, productId, quantity);
        return ResponseEntity.ok(new ApiResponse<>(200, "장바구니가 생성되었습니다.", null));
    }





}
