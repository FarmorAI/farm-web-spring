package com.farmorai.backend.service;

import com.farmorai.backend.dto.*;
import com.farmorai.backend.mapper.InquiryMapper;
import com.farmorai.backend.securityFilter.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InquiryService {
    private final InquiryMapper inquiryMapper;
    private final MemberService memberService;

    public PageResponseDto<InquiryDto> getInquiryList(PageRequestDto pageRequestDto) {
        List<InquiryDto> inquiryList = inquiryMapper.getInquiryList(pageRequestDto);
        int totalCount = inquiryMapper.getInquiryListCnt();

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

    // 문의글 조회
    public InquiryDto getInquiryById(Long inquiryId) {
        InquiryDto inquiryDto = inquiryMapper.getInquiryById(inquiryId);
        String korean = inquiryDto.getCategory().getKorean();
        inquiryDto.setCategoryKor(korean);
        inquiryMapper.increaseViewCnt(inquiryId);

        return inquiryDto;
    }

    // 문의 추가
    public ResponseEntity<String> insertInquiry(InquiryDto inquiryDto, CustomUserDetails userDetails) {
        inquiryDto.setMemberId(userDetails.getMemberId() == null ? 100L : userDetails.getMemberId());
        inquiryDto.setWriter(userDetails.getNickname());
        inquiryMapper.insertInquiry(inquiryDto);
        return ResponseEntity.ok("Success");
    }

    // 문의 수정
    public void updateInquiry(InquiryDto inquiryDto) {
        inquiryMapper.updateInquiry(inquiryDto);
    }

    // 문의 삭제
    public void deleteInquiry(Long inquiryId) {
        inquiryMapper.deleteInquiry(inquiryId);
    }

    // 유저 정보 가져오기
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return userDetails.getUsername();
        }
        return null;
    }
}
