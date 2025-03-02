package com.farmorai.backend.mapper;

import com.farmorai.backend.dto.MemberDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
public interface MemberMapper {
    List<MemberDto> getAllMember();

    MemberDto getMemberById(Long memberId);
    MemberDto getMemberByEmail(String email);
    MemberDto getMemberByNickname(String nickname);

    void insertMember(MemberDto memberDto);

    void updateMember(MemberDto memberDto);

    void deleteMember(Long memberId);

    boolean checkNickname(String nickname);

    boolean checkEmail(String email);

    void updateProfileImage(@Param("memberId") Long memberId, @Param("imageUrl") String imageUrl);
}
