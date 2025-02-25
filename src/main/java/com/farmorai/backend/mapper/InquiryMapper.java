package com.farmorai.backend.mapper;

import com.farmorai.backend.dto.InquiryDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface InquiryMapper {

    List<InquiryDto> getInquiryList();

    InquiryDto getInquiryById(Long inquiryId);
}
