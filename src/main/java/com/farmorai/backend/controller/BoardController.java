package com.farmorai.backend.controller;

import com.farmorai.backend.dto.BoardDto;

import com.farmorai.backend.dto.NoticeDto;
import com.farmorai.backend.dto.PageRequestDto;
import com.farmorai.backend.dto.PageResponseDto;


import com.farmorai.backend.securityFilter.CustomUserDetails;
import com.farmorai.backend.service.BoardService;
import com.farmorai.backend.util.FileUploadUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.FORBIDDEN;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/board")
public class BoardController {
    private final BoardService boardService;

    @GetMapping("/list")
    public ResponseEntity<PageResponseDto<BoardDto>> getNoticeList(PageRequestDto pageRequestDto) {
        return ResponseEntity.ok(boardService.getBoardList(pageRequestDto));
    }

    @GetMapping("/{boardId}")
    public BoardDto getBoardDetail(@PathVariable Long boardId){
        return boardService.getBoardDetail(boardId);
    }


    @PostMapping
    public ResponseEntity<?> insertBoard(
            @AuthenticationPrincipal CustomUserDetails userDetails, //로그인한 사용자 정보를 가져옵니다.
            @RequestBody BoardDto boardDto
    ) {
        boardService.insertBoard(userDetails.getMemberId(),boardDto);
        return ResponseEntity.ok("글 등록 성공");
    }


    @DeleteMapping("/{boardId}")
    public Map<String,String> deleteBoard(@PathVariable Long boardId){
        boardService.deleteBoard(boardId);
        return Map.of("result","success");
    }

    @PutMapping("/{boardId}")
    public Map<String,String> updateBoard(@PathVariable Long boardId, @RequestBody BoardDto boardDto){
        boardService.updateBoard(boardId, boardDto);
        return Map.of("result","success");
    }



}
