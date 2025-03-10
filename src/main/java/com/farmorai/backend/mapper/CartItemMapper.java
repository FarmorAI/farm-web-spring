package com.farmorai.backend.mapper;


import com.farmorai.backend.dto.CartItemDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CartItemMapper {

    List<CartItemDto> getCartItemList(Long cartId);

    void insertCartItem(@Param("cartId") Long cartId, @Param("productId") Long productId, @Param("quantity") int quantity);

    void deleteCartItem(@Param("cartId") Long cartId, @Param("productId") Long productId);

    void updateCartItem(@Param("cartId") Long cartId, @Param("productId") Long productId, @Param("quantity") int quantity);

    void deleteAllCartItem(Long cartId);


    boolean getCartItemExists(Long cartId, Long productId);

}
