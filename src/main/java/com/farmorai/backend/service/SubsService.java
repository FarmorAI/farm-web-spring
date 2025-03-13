package com.farmorai.backend.service;

import com.farmorai.backend.dto.SubsDto;
import com.farmorai.backend.dto.SubsStatus;
import com.farmorai.backend.mapper.SubsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class SubsService {
    private final SubsMapper subsMapper;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public String[] getDateTime() {
        LocalDateTime startDateTime = LocalDateTime.now();
        LocalDateTime endDateTime = startDateTime.plusMonths(1).toLocalDate().atStartOfDay();

        String[] dateResult = {startDateTime.format(formatter), endDateTime.format(formatter)};
        return dateResult;
    }

    public SubsDto insertSubs(Long memberId, Long planId) {
        String[] dateResult = getDateTime();

        SubsDto subsDto = new SubsDto(
            null,
            SubsStatus.ACTIVE,
            dateResult[0],
            dateResult[1],
            null,
            null,
            memberId,
            planId
        );

        subsMapper.insertSubs(subsDto);
        return subsDto;
    }

    public void cancelSubs(String token) {
        subsMapper.cancelSubs(token);
    }

    public SubsDto getSubsByMemberId(Long subsId) {
        return subsMapper.getSubsByMemberId(subsId);
    }
}
