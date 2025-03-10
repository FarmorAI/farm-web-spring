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
     * 장바구니에 상품 추가 또는 수정 (수량 변경)
     */
    public void addCart(Long memberId, Long productId, int quantity) {
        Cart cart = cartMapper.getCartById(memberId);
        cart = ensureCartExists(memberId, cart);
        addOrUpdateItem(productId, quantity, cart);
    }

    /**
     * 회원의 장바구니 목록 조회
     * @param memberId
     * @return
     */
    public List<CartItemDto> getCartItemsByMemberId(Long memberId) {
        Cart cart = cartMapper.getCartById(memberId);
        if (cart == null) {
            return Collections.emptyList(); // 빈 리스트 반환 (null 방지)
        }
        return cartItemMapper.getCartItemList(cart.getCartId());
    }

    private void addOrUpdateItem(Long productId, int quantity, Cart cart) {
        boolean exists =cartItemMapper.getCartItemExists(cart.getCartId(), productId);
        if (exists) {
            // 상품이 이미 있으면 수량만 변경
            cartItemMapper.updateCartItem(cart.getCartId(), productId, quantity);
        } else {
            cartItemMapper.insertCartItem(cart.getCartId(), productId, quantity);
        }
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

    public void deleteCartItem(Long memberId, Long productId) {
        Cart cart = cartMapper.getCartById(memberId);
        cartItemMapper.deleteCartItem(cart.getCartId(),productId);
    }
}
