package com.farmorai.backend.service;

import com.farmorai.backend.dto.AppleDto;
import com.farmorai.backend.mapper.AppleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppleService {
    private final AppleMapper appleMapper;

    public List<AppleDto> getAppleList() {
        return appleMapper.getAppleList();
    }
}
