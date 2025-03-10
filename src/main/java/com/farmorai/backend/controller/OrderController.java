package com.farmorai.backend.controller;

import com.farmorai.backend.dto.OrderItemDto;
import com.farmorai.backend.dto.OrderRequestDto;
import com.farmorai.backend.dto.OrderResponseDto;
import com.farmorai.backend.dto.response.ApiResponse;
import com.farmorai.backend.securityFilter.CustomUserDetails;
import com.farmorai.backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    //주문 생성
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponseDto>> insertOrder(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody OrderRequestDto orderRequestDto) {
        OrderResponseDto orderResponseDto = orderService.insertOrder(userDetails.getMemberId(), orderRequestDto);
        return ResponseEntity.status(CREATED).body(ApiResponse.success("주문 생성 성공", orderResponseDto, CREATED));
    }

    //주문 조회
    @GetMapping("/{orderNumber}")
    public ResponseEntity<ApiResponse<List<OrderItemDto>>> getOrderByNumber(@PathVariable String orderNumber) {
        List<OrderItemDto> orderItems = orderService.getOrderByNumber(orderNumber);
        return ResponseEntity.ok(ApiResponse.success("주문 조회 성공", orderItems));
    }

    //회원 주문 조회
    @GetMapping("/member/{memberId}")
    public ResponseEntity<ApiResponse<List<OrderResponseDto>>> getOrdersByMember(@PathVariable Long memberId) {
        List<OrderResponseDto> orderItems = orderService.getOrderByMember(memberId);
        return ResponseEntity.ok(ApiResponse.success("회원별 주문 조회 성공", orderItems));
    }


}
