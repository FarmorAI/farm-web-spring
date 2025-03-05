package com.farmorai.backend.service;

import com.farmorai.backend.dto.MemberDto;
import com.farmorai.backend.dto.MemberRole;
import com.farmorai.backend.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;

import lombok.extern.log4j.Log4j2;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.LinkedHashMap;
import java.util.List;

import java.util.Map;


@Log4j2
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;
    private final S3Service s3Service;

    @Value("${NAVERLOGIN_CLIENT_ID}")
    private String naverClientId;
    @Value("${NAVERLOGIN_CLIENT_SECRET}")
    private String naverClientSecret;

    // 프로필 이미지 업로드
    public String updateProfileImage(MultipartFile profileImage, Long memberId) {

        // S3에 이미지 업로드
        String imageUrl = s3Service.uploadFile(profileImage);
        memberMapper.updateProfileImage(memberId, imageUrl);

        return imageUrl;
    }

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
        String pwd = memberDto.getPassword();
        memberDto.setMemberRole(MemberRole.USER);
        memberDto.setPassword(passwordEncoder.encode(pwd));
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
        //accessToken 을 이용해서 사용자 정보를 가져옵니다. -> 닉네임
        String nickname = getEmailFromKakaoAccessToken(accessToken);

        //기존에 DB에 회원 정보가 있는 경우
        boolean checkNickname = memberMapper.checkNickname(nickname);
        if(checkNickname){
            MemberDto memberDto = memberMapper.getMemberByNickname(nickname);//기존의 멤버 정보를 반환합니다.
            log.info("memberDto = {}", memberDto);
            return memberDto;
        }

        //DB에 회원 정보가 없는 경우
        MemberDto socialMember = makeSocialMember(nickname);
        memberMapper.insertMember(socialMember);

        return socialMember;
    }

    //소셜 로그인 구글
    public MemberDto getGoogleMember(String accessToken) {
        String googleGetUserURL = "https://www.googleapis.com/oauth2/v3/userinfo";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                googleGetUserURL, HttpMethod.POST, entity, Map.class);

        Map<String, Object> googleUser = response.getBody();
        String email = (String) googleUser.get("email");
        String name = (String) googleUser.get("name");


        // DB에서 회원 정보 조회
        MemberDto existingMember = memberMapper.getMemberByEmail(email);
        if (existingMember != null) {
            log.info("기존 회원 로그인 처리");
            return existingMember;
        }


        // 신규 회원 가입 처리
        MemberDto newMember = MemberDto.builder()
                .email(email)
                .name(name)
                .nickname(name)
                .password(passwordEncoder.encode(makeTempPassword()))
                .memberRole(MemberRole.USER)
                .social(true)
                .build();
        memberMapper.insertMember(newMember);
        return newMember;
    }


    private MemberDto makeSocialMember(String nickname){
        return MemberDto.builder()
                .email(nickname+"@kakao.com")
                .name("Social Member")
                .password(passwordEncoder.encode(makeTempPassword()))
                .nickname(nickname)
                .memberRole(MemberRole.USER)
                .social(true)
                .build();
    }

    private String getEmailFromKakaoAccessToken(String accessToken){
        String kakaoGetUserURL = "https://kapi.kakao.com/v2/user/me";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization","Bearer "+ accessToken);
        headers.add("Content-Type","application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<?> entity = new HttpEntity<>(headers);

        UriComponents uriBuilder = UriComponentsBuilder.fromHttpUrl(kakaoGetUserURL).build();

        LinkedHashMap<String,LinkedHashMap> kakaoResult = restTemplate.exchange(uriBuilder.toUri(), HttpMethod.POST,entity,LinkedHashMap.class).getBody();

        log.info("--------------------------");
        log.info(kakaoResult);
        LinkedHashMap<String,String> properties = kakaoResult.get("properties");

        String nickname = properties.get("nickname");
        log.info("nickname = {}", nickname);

        return nickname;
    }

    // =======================네이버 소셜 로그인==============================
    public MemberDto getNaverMember(String accessToken) {
        // 네이버 API를 호출하여 사용자 프로필 정보 가져오기
        LinkedHashMap<String, Object> profile = getNaverUserProfile(accessToken);
        String email = (String) profile.get("email");
        String nickname = (String) profile.get("nickname");

        // 기존에 DB에 회원 정보가 있는 경우 (이메일을 기준으로 체크)
        if (memberMapper.checkEmail(email)) {
            MemberDto memberDto = memberMapper.getMemberByEmail(email);
            log.info("memberDto = {}", memberDto);
            return memberDto;
        }

        // DB에 회원 정보가 없는 경우 신규 회원 등록
        MemberDto socialMember = makeSocialMemberForNaver(email, nickname);
        memberMapper.insertMember(socialMember);
        return socialMember;
    }

    // 네이버 API를 호출하여 사용자 프로필 정보 추출 (response 에는 email, nickname 등 포함)
    private LinkedHashMap<String, Object> getNaverUserProfile(String accessToken) {
        String naverGetUserURL = "https://openapi.naver.com/v1/nid/me";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-Type", "application/json");
        // 클라이언트 ID와 Secret을 헤더에 추가 (필요한 경우)
        headers.add("X-Naver-Client-Id", naverClientId);
        headers.add("X-Naver-Client-Secret", naverClientSecret);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(naverGetUserURL).build();

        LinkedHashMap<String, Object> naverResult = restTemplate.exchange(uriComponents.toUri(), HttpMethod.GET, entity, LinkedHashMap.class).getBody();

        log.info("Naver response: {}", naverResult);

        // 네이버 API 응답 형식: { "resultcode": "00", "message": "success", "response": { ... } }
        LinkedHashMap<String, Object> profile = (LinkedHashMap<String, Object>) naverResult.get("response");

        return profile;
    }

    private MemberDto makeSocialMemberForNaver(String email, String nickname) {
        String tempPassword = makeTempPassword();
        log.info("tempPassword = {}", tempPassword);
        return MemberDto.builder()
                .email(email)
                .name("Social Member")
                .password(passwordEncoder.encode(tempPassword))
                .nickname(nickname)
                .memberRole(MemberRole.USER)
                .social(true)
                .build();
    }
    //==============================================================================

    private String makeTempPassword() {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            buffer.append((char) ((Math.random() * 55) + 65));
        }
        return buffer.toString();
    }

    // 닉네임 중복 검사
    public boolean checkNickname(String nickname) {
        return memberMapper.checkNickname(nickname);
    }

    // 이메일 중복 검사
    public boolean checkEmail(String email) {
        return memberMapper.checkEmail(email);
    }


}
