package com.farmorai.backend.mapper;

import com.farmorai.backend.dto.AiResultDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AiResultMapper {
    List<AiResultDto> getAiResultList(Long memberId);

    AiResultDto getAiResult(Long aiResultId);

    void insertAiResult(AiResultDto aiResultDto);

    void deleteAiResult(Long aiResultId);
}
