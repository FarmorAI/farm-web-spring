package com.farmorai.backend.mapper;

import com.farmorai.backend.dto.PaymentDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface PaymentMapper {
    List<PaymentDto> getAllPayment();

    void insertPayment(PaymentDto paymentDto);

    List<PaymentDto> getPaymentById(PaymentDto paymentDto);
}
