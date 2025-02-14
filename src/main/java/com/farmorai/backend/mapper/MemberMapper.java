package com.farmorai.backend.mapper;

import com.farmorai.backend.domain.Member;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper {
    Member getMemberById(Long id);
}
