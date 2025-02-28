package com.farmorai.backend.controller;

import com.farmorai.backend.dto.BoardDto;
import com.farmorai.backend.dto.NoticeDto;
import com.farmorai.backend.dto.PageRequestDto;
import com.farmorai.backend.dto.PageResponseDto;
import com.farmorai.backend.service.BoardService;
import com.farmorai.backend.util.FileUploadUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/board")
public class BoardController {
    private final BoardService boardService;
    private final FileUploadUtil fileUploadUtil = new FileUploadUtil();

    @Value("${com.farmorai.upload.path}")
    private String uploadPath;

    @GetMapping("/list")
    public ResponseEntity<PageResponseDto<BoardDto>> getNoticeList(PageRequestDto pageRequestDto) {
        return ResponseEntity.ok(boardService.getBoardList(pageRequestDto));
    }

    @GetMapping("/{boardId}")
    public BoardDto getBoardDetail(@PathVariable Long boardId){
        return boardService.getBoardDetail(boardId);
    }


    @PostMapping
    public Map<String, Object> insertBoard(
            @RequestParam("board") String boardJson,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {

        try {
            // JSON을 DTO로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            BoardDto boardDto = objectMapper.readValue(boardJson, BoardDto.class);

            // 파일 저장
            List<String> fileNames = new ArrayList<>();
            if (files != null && !files.isEmpty()) {
                fileNames = fileUploadUtil.saveFiles(files);
            }

            // 게시글 데이터 저장
            boardService.insertBoard(boardDto);

            // 응답 데이터 반환 (fileNames이 null이면 빈 리스트로 초기화)
            return Map.of(
                    "result", "success",
                    "uploadedFiles", fileNames != null ? fileNames : new ArrayList<>()
            );
        } catch (IOException e) {
            log.error("Failed to parse board JSON", e);
            return Map.of("result", "fail", "message", "Invalid board data");
        } catch (RuntimeException e) {
            log.error("File upload failed", e);
            return Map.of("result", "fail", "message", e.getMessage());
        }
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
