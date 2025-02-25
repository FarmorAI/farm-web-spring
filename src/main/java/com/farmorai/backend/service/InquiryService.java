package com.farmorai.backend.service;

import com.farmorai.backend.dto.InquiryDto;
import com.farmorai.backend.mapper.InquiryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InquiryService {
    private final InquiryMapper inquiryMapper;

    public List<InquiryDto> getInquiryList() {
        return inquiryMapper.getInquiryList();
    }

    public InquiryDto getInquiryById(Long inquiryId) {
        return inquiryMapper.getInquiryById(inquiryId);
    }
}
