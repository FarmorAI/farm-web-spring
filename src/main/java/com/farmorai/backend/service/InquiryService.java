package com.farmorai.backend.service;

import com.farmorai.backend.dto.InquiryCategory;
import com.farmorai.backend.dto.InquiryDto;
import com.farmorai.backend.dto.PageRequestDto;
import com.farmorai.backend.dto.PageResponseDto;
import com.farmorai.backend.mapper.InquiryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InquiryService {
    private final InquiryMapper inquiryMapper;

    public PageResponseDto<InquiryDto> getInquiryList(PageRequestDto pageRequestDto) {
        List<InquiryDto> inquiryList = inquiryMapper.getInquiryList(pageRequestDto);
        int totalCount = inquiryMapper.getInquiryListCount();

        // 각 InquiryDto 항목에 대해 한글 카테고리 설정
        for (InquiryDto inquiryDto : inquiryList) {
            if (inquiryDto.getCategory() != null) {
                String korean = inquiryDto.getCategory().getKorean();
                inquiryDto.setCategoryKor(korean);
            }
        }
        return PageResponseDto.<InquiryDto>builder()
                .dtoList(inquiryList)
                .pageRequestDto(pageRequestDto)
                .total(totalCount)
                .build();
    }


    public InquiryDto getInquiryById(Long inquiryId) {
        InquiryDto inquiryDto = inquiryMapper.getInquiryById(inquiryId);
        String korean = inquiryDto.getCategory().getKorean();
        inquiryDto.setCategoryKor(korean);

        return inquiryMapper.getInquiryById(inquiryId);
    }

    // 문의 추가
    public void insertInquiry(InquiryDto inquiryDto) {
        inquiryMapper.insertInquiry(inquiryDto);
    }

    // 문의 수정
    public void updateInquiry(InquiryDto inquiryDto) {
        inquiryMapper.updateInquiry(inquiryDto);
    }

    // 문의 삭제
    public void deleteInquiry(Long inquiry_id) {
        inquiryMapper.deleteInquiry(inquiry_id);
    }
}
