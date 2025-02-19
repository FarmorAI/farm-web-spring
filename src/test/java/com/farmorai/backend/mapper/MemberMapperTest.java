package com.farmorai.backend.mapper;

import com.farmorai.backend.dto.MemberDto;
import com.farmorai.backend.dto.MemberRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberMapperTest {

    @Autowired
    private MemberMapper memberMapper;

    private MemberDto testMember;

    @BeforeEach
    void setUp() {
        // 테스트용 회원 정보 생성
        testMember = new MemberDto(
                null,
                "test@email.com",
                "password",
                "name",
                "nickname",
                "010-1234-5678",
                "2000-01-01",
                MemberRole.USER,
                "address",
                null,
                null
        );
    }


    @Test
    @DisplayName("DB연동_조회_삽입_테스트")
    void getAllMemberTest() {
        // 초기 회원 수 확인
        List<MemberDto> initialMembers = memberMapper.getAllMember();
        int initialSize = initialMembers.size();

        // 회원 저장
        memberMapper.insertMember(testMember);

        // 회원 저장 후 전체 조회
        List<MemberDto> updatedMembers = memberMapper.getAllMember();

        // 검증
        assertThat(updatedMembers.size()).isEqualTo(initialSize + 1);
    }


    @Test
    @DisplayName("회원_조회_byEmail")
    void getMemberByEmailTest() {
        // 회원 저장
        memberMapper.insertMember(testMember);

        // ID로 회원 조회
        MemberDto foundMember = memberMapper.getMemberByEmail(testMember.getEmail());

        // 검증
        assertThat(foundMember)
                .isNotNull()
                .extracting(MemberDto::getEmail, MemberDto::getNickname)
                .containsExactly(testMember.getEmail(), testMember.getNickname());
    }
}