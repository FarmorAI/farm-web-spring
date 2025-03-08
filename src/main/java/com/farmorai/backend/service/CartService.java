package com.farmorai.backend.service;

import com.farmorai.backend.domain.Cart;
import com.farmorai.backend.dto.CartItemDto;
import com.farmorai.backend.mapper.CartItemMapper;
import com.farmorai.backend.mapper.CartMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemMapper cartItemMapper;
    private final CartMapper cartMapper;

    /**
     * 장바구니에 상품 추가
     */
    public void addCart(Long memberId, Long productId, int quantity) {
        // 장바구니 조회
        Cart cart = cartMapper.getCartById(memberId);
        cart = ensureCartExists(memberId, cart);
        // 상품 추가
        cartItemMapper.insertCartItem(cart.getCartId(),productId,quantity);

    }

    private Cart ensureCartExists(Long memberId, Cart cart) {
        if (cart == null) {
            // 장바구니가 없으면 생성
            cartMapper.insertCart(memberId);
            cart = cartMapper.getCartById(memberId);
        }
        // 장바구니가 있으면 리턴
        return cart;
    }


    public List<CartItemDto> getCartItemsByMemberId(Long memberId) {
        Cart cart = cartMapper.getCartById(memberId);
        if (cart == null) {
            return Collections.emptyList(); // 빈 리스트 반환 (null 방지)
        }
        return cartItemMapper.getCartItemList(cart.getCartId());
    }
}
