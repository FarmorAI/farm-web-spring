package com.farmorai.backend.service;

import com.farmorai.backend.domain.Cart;
import com.farmorai.backend.mapper.CartItemMapper;
import com.farmorai.backend.mapper.CartMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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


}
