package com.farmorai.backend.controller;

import com.farmorai.backend.dto.BoardDto;
import com.farmorai.backend.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardController {
    private final BoardService boardService;
    @GetMapping
    public List<BoardDto> getBoardList(){
        return boardService.getBoardList();
    }

    @GetMapping("/{boardId}")
    public BoardDto getBoardDetail(@PathVariable Long boardId){
        return boardService.getBoardDetail(boardId);
    }

    @PostMapping
    public Map<String,String> insertBoard(@RequestBody BoardDto boardDto){
        boardService.insertBoard(boardDto);
        return Map.of("result","success");
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
