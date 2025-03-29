package com.farmorai.backend.mapper;

import com.farmorai.backend.dto.AiAppleResultDto;
import com.farmorai.backend.dto.AiResultDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AiResultMapper {
    List<AiResultDto> getAiResultList(Long memberId);

    List<AiAppleResultDto> getAppleResults(Long aiResultId); //개별 사과 조회

    AiResultDto getAiResult(Long aiResultId);

    void insertAiResult(AiResultDto aiResultDto);

    void deleteAiResult(Long aiResultId);

    void insertAppleResults(List<AiAppleResultDto> appleDtoList);
}
