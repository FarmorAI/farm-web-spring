package com.farmorai.backend.mapper;

import com.farmorai.backend.domain.Cart;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface CartMapper {

    Cart getCartById(Long memberId);

    void insertCart(Long memberId);

    void deleteCart(Long cartId);

    void deleteCartItem(String orderNumber);
}
