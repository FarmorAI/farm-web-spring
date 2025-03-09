package com.farmorai.backend.mapper;

import com.farmorai.backend.domain.Order;
import com.farmorai.backend.dto.OrderItemDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderMapper {
    void insertOrder(Order order);

    void insertOrderItems(List<OrderItemDto> orderItems);
}
