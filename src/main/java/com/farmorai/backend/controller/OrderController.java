package com.farmorai.backend.controller;

import com.farmorai.backend.dto.OrderRequestDto;
import com.farmorai.backend.dto.OrderResponseDto;
import com.farmorai.backend.dto.response.ApiResponse;
import com.farmorai.backend.securityFilter.CustomUserDetails;
import com.farmorai.backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.*;

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


}
