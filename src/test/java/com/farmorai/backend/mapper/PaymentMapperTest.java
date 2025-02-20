package com.farmorai.backend.mapper;

import com.farmorai.backend.dto.PaymentDto;
import com.farmorai.backend.dto.PaymentMethod;
import com.farmorai.backend.dto.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class PaymentMapperTest {

    @Autowired
    private PaymentMapper paymentMapper;

    private PaymentDto testDto;

    @BeforeEach
    void setUp() {
        testDto = new PaymentDto(
                null,
                1000,
                "token",
                null,
                PaymentMethod.NAVERPAY,
                PaymentStatus.COMPLETED,
                1L,
                1L,
                1L
        );
    }

    @Test
    @DisplayName("DB연동_조회_삽입_테스트")
    void getAllMemberTest() {
        // 결제 내역 확인
        List<PaymentDto> payments = paymentMapper.getAllPayment();
        paymentMapper.insertPayment(testDto);
        List<PaymentDto> payments2 = paymentMapper.getAllPayment();

        // 검증
        assertThat(payments2.size()).isEqualTo(payments.size() + 1);
    }
}