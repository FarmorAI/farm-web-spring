package com.farmorai.backend.mapper;

import com.farmorai.backend.dto.MyPageDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MyPageMapper {
    MyPageDto getMyInfo(@Param("memberId") Long memberId);
}
