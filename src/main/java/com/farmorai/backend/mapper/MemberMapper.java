package com.farmorai.backend.mapper;

import com.farmorai.backend.dto.MemberDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MemberMapper {
    List<MemberDto> getAllMember();

    MemberDto getMemberById(Long memberId);

    void insertMember(MemberDto memberDto);

    void updateMember(MemberDto memberDto);

    void deleteMember(Long memberId);
}
