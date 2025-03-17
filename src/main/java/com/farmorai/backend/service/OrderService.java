package com.farmorai.backend.service;

import com.farmorai.backend.domain.Order;
import com.farmorai.backend.dto.OrderItemDto;
import com.farmorai.backend.dto.OrderRequestDto;
import com.farmorai.backend.dto.OrderResponseDto;
import com.farmorai.backend.dto.OrderStatus;
import com.farmorai.backend.mapper.CartMapper;
import com.farmorai.backend.mapper.OrderMapper;
import com.farmorai.backend.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.farmorai.backend.util.OrderUtil.generateOrderNumber;
import static java.util.stream.Collectors.groupingBy;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderMapper orderMapper;
    private final ProductMapper productMapper;
    private final CartMapper cartMapper;

    @Transactional
    public OrderResponseDto insertOrder(Long memberId, OrderRequestDto orderRequestDto) {
        // 주문 생성 로직
        Order order = createOrder(memberId, orderRequestDto);

        // 주문 상세 생성 로직
        List<OrderItemDto> orderItems = createOrderDetails(orderRequestDto, order);

        return getOrderResponseDto(order, orderItems);
    }

    @Transactional
    public void updateOrderStatus(String orderNumber, String paymentId) {
        orderMapper.updateOrderStatus(orderNumber, paymentId, OrderStatus.PAID);
    }

    @Transactional
    public void processOrderAfterPayment(String orderNumber, String paymentId) {
        List<OrderItemDto> orderItems = orderMapper.getOrderByNumber(orderNumber);
        if (orderItems.isEmpty()) {
            throw new RuntimeException("No order items found for order number: " + orderNumber);
        }
        orderMapper.updateOrderStatus(orderNumber, paymentId, OrderStatus.PAID);
        orderItems.forEach(item -> {
            productMapper.decreaseStock(item.getProductId(), item.getQuantity());
        });
        cartMapper.deleteCartItem(orderNumber);
    }

    // 주문 조회 (주문 번호)
    public List<OrderItemDto> getOrderByNumber(String orderNumber) {
        return orderMapper.getOrderByNumber(orderNumber);
    }

    // 회원 주문 조회
    public List<OrderResponseDto> getOrderByMember(Long memberId) {
        return getOrderResponseDtoList(memberId);
    }

    private static OrderResponseDto getOrderResponseDto(Order order, List<OrderItemDto> orderItems) {
        return OrderResponseDto.builder()
                .ordersId(order.getOrdersId())
                .totalAmount(order.getTotalAmount())
                .shippingFee(order.getShippingFee())
                .status(order.getStatus())
                .orderItems(orderItems)
                .orderNumber(order.getOrderNumber())
                .build();
    }

    private List<OrderItemDto> createOrderDetails(OrderRequestDto orderRequestDto, Order order) {
        List<OrderItemDto> orderItems = orderRequestDto.getOrderItems().stream()
                .map(orderItemDto -> OrderItemDto.builder()
                        .ordersId(order.getOrdersId())
                        .productId(orderItemDto.getProductId())
                        .quantity(orderItemDto.getQuantity())
                        .price(orderItemDto.getPrice())
                        .build())
                .toList();

        orderMapper.insertOrderItems(orderItems);
        return orderItems;
    }

    private Order createOrder(Long memberId, OrderRequestDto orderRequestDto) {
        Order order = Order.builder()
                .memberId(memberId)
                .totalAmount(orderRequestDto.getTotalAmount())
                .shippingFee(orderRequestDto.getShippingFee())
                .orderNumber(generateOrderNumber())
                .status(OrderStatus.PENDING)
                .build();

        orderMapper.insertOrder(order);
        return order;
    }

    private List<OrderResponseDto> getOrderResponseDtoList(Long memberId) {
        List<OrderItemDto> orderByMember = orderMapper.getOrderByMember(memberId);
        return orderByMember.stream()
                .collect(groupingBy(OrderItemDto::getOrdersId))
                .values().stream()
                .map(orderItemDtos -> {
                    OrderItemDto orderItemDto = orderItemDtos.getFirst();
                    return OrderResponseDto.builder()
                            .ordersId(orderItemDto.getOrdersId())
                            .orderNumber(orderItemDto.getOrderNumber())
                            .status(orderItemDto.getStatus())
                            .totalAmount(orderItemDto.getTotalAmount())
                            .createdAt(orderItemDto.getCreatedAt())
                            .orderItems(orderItemDtos)
                            .build();
                })
                .toList();
    }
}
