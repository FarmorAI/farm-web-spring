package com.farmorai.backend.service;

import com.farmorai.backend.dto.MemberDto;
import com.farmorai.backend.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.LinkedHashMap;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberMapper memberMapper;

    // 전체 회원 조회
    public List<MemberDto> getAllMember() {
        return memberMapper.getAllMember();
    }

    // 회원 조회 (ID)
    public MemberDto getMemberById(Long memberId) {
        return memberMapper.getMemberById(memberId);
    }

    // 회원 조회 (Email)
    public MemberDto getMemberByEmail(String email) {
        return memberMapper.getMemberByEmail(email);
    }

    // 회원 등록
    public void insertMember(MemberDto memberDto) {
        memberMapper.insertMember(memberDto);
    }

    // 회원 수정
    public void updateMember(MemberDto memberDto) {
        memberMapper.updateMember(memberDto);
    }

    // 회원 삭제
    public void deleteMember(Long memberId) {
        memberMapper.deleteMember(memberId);
    }

    //소셜 로그인 카카오
    public MemberDto getKakaoMember(String accessToken){
        //accessToken 을 이용해서 사용자 정보를 가져옵니다.
        getEmailFromKakaoAcessToken(accessToken);

        //기존에 DB에 회원 정보가 있는 경우 / 없는 경우

        return null;
    }

    private void getEmailFromKakaoAcessToken(String accessToken){
        String kakaoGetUserURL = "https://kapi.kakao.com/v2/user/me";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization","Bearer "+ accessToken);
        headers.add("Content-Type","application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<Object> entity = new HttpEntity<>(headers);

        UriComponents uriBuilder = UriComponentsBuilder.fromHttpUrl(kakaoGetUserURL).build();

        ResponseEntity<LinkedHashMap> response =
                restTemplate.exchange(uriBuilder.toUri(), HttpMethod.POST,entity,LinkedHashMap.class);
        log.info(response);

        LinkedHashMap<String,LinkedHashMap> bodyMap = response.getBody();

        log.info("--------------------------");
        log.info(bodyMap);


    }

}
