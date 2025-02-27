package com.farmorai.backend.mapper;

import com.farmorai.backend.dto.InquiryDto;
import com.farmorai.backend.dto.PageRequestDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface InquiryMapper {

    List<InquiryDto> getInquiryList(PageRequestDto pageRequestDto);
    int getInquiryListCnt();

    InquiryDto getInquiryById(Long inquiryId);
    void increaseViewCnt(Long inquiryId);

    void insertInquiry(InquiryDto inquiryDto);

    void updateInquiry(InquiryDto inquiryDto);

    void deleteInquiry(Long inquiryId);
}
