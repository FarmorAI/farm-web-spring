package com.farmorai.backend.service;

import com.farmorai.backend.dto.*;
import com.farmorai.backend.mapper.MemberMapper;
import com.farmorai.backend.mapper.PaymentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentMapper paymentMapper;
    private final MemberMapper memberMapper;

    // PaymentDto 조회
    public PaymentDto getPaymentById(Long paymentId) {
        PaymentDto paymentDto = paymentMapper.getPaymentById(paymentId);
        return paymentDto;
    }

    // PENDING : PaymentDto 생성 및 레코드 저장
    public PaymentDto insertPayment(String email, String subsPlan, String subsPrice) {
        MemberDto memberDto = memberMapper.getMemberByEmail(email);
        Long planId = (long) PaymentPlan.valueOf(subsPlan).ordinal();

        PaymentDto paymentDto = new PaymentDto(
                null,
                Integer.parseInt(subsPrice),
                null,
                null,
                PaymentMethod.NAVERPAY,
                PaymentStatus.PENDING,
                planId,
                memberDto.getMemberId(),
                null
        );

        paymentMapper.insertPayment(paymentDto);
        return paymentDto;
    }

    // Payment 레코드 저장 (Completed)
    public void updatePayment(PaymentDto paymentDto, String paymentId, Long subsId) {
        paymentDto.setToken(paymentId);
        paymentDto.setStatus(PaymentStatus.COMPLETED);
        paymentDto.setSubsId(subsId);
        paymentMapper.updatePayment(paymentDto);
    }

    // Payment Update
    public void refundPayment(String token) {
        paymentMapper.refundPayment(token);
    }
}
