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
}
