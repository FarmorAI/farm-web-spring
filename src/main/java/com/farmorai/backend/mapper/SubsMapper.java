package com.farmorai.backend.mapper;

import com.farmorai.backend.dto.SubsDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SubsMapper {
    List<SubsDto> getAllSubs();

    SubsDto getSubsByMemberId(Long memberId);

    void insertSubs(SubsDto subsDto);

    void cancelSubs(String token);
}
