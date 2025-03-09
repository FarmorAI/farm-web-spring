package com.farmorai.backend.service;

import com.farmorai.backend.domain.Order;
import com.farmorai.backend.dto.OrderItemDto;
import com.farmorai.backend.dto.OrderRequestDto;
import com.farmorai.backend.dto.OrderResponseDto;
import com.farmorai.backend.dto.OrderStatus;
import com.farmorai.backend.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.farmorai.backend.util.OrderUtil.generateOrderNumber;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderMapper orderMapper;


    public OrderResponseDto insertOrder(Long memberId, OrderRequestDto orderRequestDto) {
        // 주문 생성 로직
        Order order = Order.builder()
                .memberId(memberId)
                .totalAmount(orderRequestDto.getTotalAmount())
                .shippingFee(orderRequestDto.getShippingFee())
                .orderNumber(generateOrderNumber())
                .status(OrderStatus.PENDING)
                .build();

        orderMapper.insertOrder(order);

        List<OrderItemDto> orderItems = orderRequestDto.getOrderItems().stream()
                .map(orderItemDto -> OrderItemDto.builder()
                        .ordersId(order.getOrdersId())
                        .productId(orderItemDto.getProductId())
                        .quantity(orderItemDto.getQuantity())
                        .price(orderItemDto.getPrice())
                        .build())
                .toList();

        orderMapper.insertOrderItems(orderItems);

        return OrderResponseDto.builder()
                .orderId(order.getOrdersId())
                .totalAmount(order.getTotalAmount())
                .shippingFee(order.getShippingFee())
                .status(order.getStatus())
                .orderItems(orderItems)
                .build();
    }
}
