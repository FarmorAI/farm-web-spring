package com.farmorai.backend.controller;

import com.farmorai.backend.dto.BoardDto;
import com.farmorai.backend.service.BoardService;
import com.farmorai.backend.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    @GetMapping
    public List<BoardDto> getBoardList(){
        return boardService.getBoardList();
    }

    @GetMapping("/{boardId}")
    public BoardDto getBoardDetail(@PathVariable Long boardId){
        return boardService.getBoardDetail(boardId);
    }

    @PostMapping
    public Map<String, Object> insertBoard(
            @RequestPart("board") BoardDto boardDto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {

        try {
            List<String> fileNames = null;

            if (files != null && !files.isEmpty()) {
                fileNames = fileUploadUtil.saveFiles(files); // 다중 파일 업로드
            }

            boardService.insertBoard(boardDto); // 게시글 데이터 저장 (파일 정보는 저장하지 않음)

            return Map.of(
                    "result", "success",
                    "uploadedFiles", fileNames // 업로드된 파일명 반환
            );
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
