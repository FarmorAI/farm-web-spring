package com.farmorai.backend.controller;

import com.farmorai.backend.dto.MyPageDto;
import com.farmorai.backend.securityFilter.CustomUserDetails;
import com.farmorai.backend.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class MyPageController {
    private final MyPageService mypageService;

    @GetMapping("/{memberId}")
    public ResponseEntity<MyPageDto> getMyInfo(
            @PathVariable Long memberId){
        MyPageDto infoData = mypageService.getMyInfo(memberId);
        return ResponseEntity.ok(infoData);

    }

}
