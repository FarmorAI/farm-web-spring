package com.farmorai.backend.mapper;

import com.farmorai.backend.dto.PaymentDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PaymentMapper {
    List<PaymentDto> getAllPayment();

    void insertPayment(PaymentDto paymentDto);
}
