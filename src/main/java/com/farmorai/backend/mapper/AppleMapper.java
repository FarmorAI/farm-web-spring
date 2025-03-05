package com.farmorai.backend.mapper;

import com.farmorai.backend.dto.AppleDto;
import com.farmorai.backend.dto.BoardDto;
import com.farmorai.backend.dto.PageRequestDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AppleMapper {
    List<AppleDto> getAppleList();
}
