package com.farmorai.backend.service;

import com.farmorai.backend.dto.*;
import com.farmorai.backend.mapper.MemberMapper;
import com.farmorai.backend.mapper.PaymentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentMapper paymentMapper;
    private final MemberMapper memberMapper;

    public PaymentDto insertPayment(String email, String subPlan, String subPrice) {
        // memberDto
        MemberDto memberDto = memberMapper.getMemberByEmail(email);

        // paymentDto
        PaymentDto paymentDto = new PaymentDto(
                null,
                Integer.parseInt(subPrice),
                null,
                null,
                PaymentMethod.NAVERPAY,
                PaymentStatus.PENDING,
                Long.parseLong(String.valueOf(subPlan)),
                memberDto.getMemberId(),
                null
        );

        paymentMapper.insertPayment(paymentDto);
        return paymentDto;
    }
}
