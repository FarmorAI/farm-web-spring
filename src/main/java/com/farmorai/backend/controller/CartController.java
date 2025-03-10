package com.farmorai.backend.controller;

import com.farmorai.backend.dto.CartItemDto;
import com.farmorai.backend.dto.response.ApiResponse;
import com.farmorai.backend.securityFilter.CustomUserDetails;
import com.farmorai.backend.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> addCart(@AuthenticationPrincipal CustomUserDetails userDetails, Long productId, int quantity) {
        cartService.addCart(userDetails.getMemberId(), productId, quantity);
        return ResponseEntity.ok(ApiResponse.success("장바구니가 생성되었습니다.", null));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CartItemDto>>> getCartItemsByMember(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<CartItemDto> cartItems = cartService.getCartItemsByMemberId(userDetails.getMemberId());
        return ResponseEntity.ok(ApiResponse.success("장바구니 목록 조회 성공", cartItems));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<String>> deleteCartItem(@AuthenticationPrincipal CustomUserDetails userDetails, Long productId) {
        cartService.deleteCartItem(userDetails.getMemberId(), productId);
        return ResponseEntity.ok(ApiResponse.success( "장바구니 상품 삭제 성공", null));
    }





}
