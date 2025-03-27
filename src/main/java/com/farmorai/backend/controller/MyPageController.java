package com.farmorai.backend.controller;

import com.farmorai.backend.dto.MyPageDto;
import com.farmorai.backend.securityFilter.CustomUserDetails;
import com.farmorai.backend.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class MyPageController {
    private final MyPageService mypageService;

    @GetMapping("/{memberId}")
    public ResponseEntity<MyPageDto> getMyInfo(@PathVariable Long memberId){
        MyPageDto infoData = mypageService.getMyInfo(memberId);
        return ResponseEntity.ok(infoData);

    }

    @GetMapping("/list")
    public ResponseEntity<List<Map<String, Object>>> getMemberDataList(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails.getMemberId();
        List<Map<String, Object>> memberDataList = mypageService.getMemberDataList(memberId);

        // If no data is found, return 404
        if (memberDataList == null || memberDataList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Return 200 OK with the list of maps
        return ResponseEntity.ok(memberDataList);
    }
}
