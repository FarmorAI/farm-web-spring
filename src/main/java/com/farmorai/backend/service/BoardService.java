package com.farmorai.backend.service;

import com.farmorai.backend.dto.BoardDto;
import com.farmorai.backend.mapper.BoardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BoardService {
    private final BoardMapper boardMapper;

    public List<BoardDto> getBoardList(){
        return boardMapper.getBoardList();
    };

    @Transactional
    public void deleteBoard(Long boardId) {
        boardMapper.deleteBoard(boardId);
    }

    @Transactional
    public void insertBoard(BoardDto boardDto) {
        boardMapper.insertBoard(boardDto);
    }
    @Transactional
    public void updateBoard(Long boardId, BoardDto boardDto) {
        boardMapper.updateBoard(boardId, boardDto);
    }

    @Transactional
    public BoardDto getBoardDetail(Long boardId) {
        boardMapper.updateBoardViews(boardId);
        return boardMapper.getBoardDetail(boardId);
    }
}
