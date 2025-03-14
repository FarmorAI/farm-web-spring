package com.farmorai.backend.service;

import com.farmorai.backend.dto.MyPageDto;
import com.farmorai.backend.mapper.MyPageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MyPageService {
    private final MyPageMapper mypageMapper;

    public MyPageDto getMyInfo(Long memberId) {
        MyPageDto infoData = mypageMapper.getMyInfo(memberId);

        if (infoData != null) {
            if (infoData.getBoardTitles() != null && infoData.getBoardCreatedDates() != null) {
                String[] boardTitles = infoData.getBoardTitles().split(", ");
                String[] boardCreatedDates = infoData.getBoardCreatedDates().split(", ");

                List<Map<String, Object>> boardInfoList = new ArrayList<>();
                for (int i = 0; i < boardTitles.length; i++) {
                    Map<String, Object> boardInfo = new HashMap<>();
                    boardInfo.put("title", boardTitles[i]);
                    boardInfo.put("created_at", boardCreatedDates[i]);
                    boardInfoList.add(boardInfo);
                }

                infoData.setBoardInfo(boardInfoList);
            }

            if (infoData.getInquiryTitles() != null && infoData.getInquiryCreatedDates() != null) {
                String[] inquiryTitles = infoData.getInquiryTitles().split(", ");
                String[] inquiryCreatedDates = infoData.getInquiryCreatedDates().split(", ");

                List<Map<String, Object>> inquiryInfoList = new ArrayList<>();
                for (int i = 0; i < inquiryTitles.length; i++) {
                    Map<String, Object> inquiryInfo = new HashMap<>();
                    inquiryInfo.put("title", inquiryTitles[i]);
                    inquiryInfo.put("created_at", inquiryCreatedDates[i]);
                    inquiryInfoList.add(inquiryInfo);
                }

                infoData.setInquiryInfo(inquiryInfoList);
            }

            return infoData;
        }

        return null;
    }

    public List<Map<String, Object>> getMemberDataList(Long memberId) {
        MyPageDto infoData = getMyInfo(memberId);
        List<Map<String, Object>> resultList = new ArrayList<>();

        if (infoData != null) {
            // 1. memberData 처리
            Map<String, Object> memberData = new HashMap<>();
            memberData.put("email", infoData.getEmail());
            memberData.put("nickname", infoData.getNickname());
            memberData.put("phone", infoData.getPhone());
            memberData.put("address", infoData.getAddress());
            memberData.put("memberCreatedAt", infoData.getMemberCreatedAt());

            // memberData를 resultList에 추가
            resultList.add(memberData);

            // 2. subscriptionData 처리
            Map<String, Object> subscriptionData = new HashMap<>();
            subscriptionData.put("planId", infoData.getPlanId());
            subscriptionData.put("startDate", infoData.getStartDate());
            subscriptionData.put("endDate", infoData.getEndDate());

            // subscriptionData를 resultList에 추가
            resultList.add(subscriptionData);

            // 3. boardInfo 처리
            List<Map<String, Object>> boardInfoList = infoData.getBoardInfo() != null ? infoData.getBoardInfo() : new ArrayList<>();
            if (!boardInfoList.isEmpty()) {
                resultList.add(Map.of("boardInfo", boardInfoList));
            }

            // 4. inquiryInfo 처리
            List<Map<String, Object>> inquiryInfoList = infoData.getInquiryInfo() != null ? infoData.getInquiryInfo() : new ArrayList<>();
            if (!inquiryInfoList.isEmpty()) {
                resultList.add(Map.of("inquiryInfo", inquiryInfoList));
            }
        }

        return resultList;
    }
}
