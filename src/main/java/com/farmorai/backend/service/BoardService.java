package com.farmorai.backend.service;

import com.farmorai.backend.dto.BoardDto;
import com.farmorai.backend.mapper.BoardMapper;
import com.farmorai.backend.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BoardService {
    private final BoardMapper boardMapper;
    private final FileUploadUtil fileUploadUtil;

    public Map<String, Object> getBoardList(int page, int size) {
        int offset = (page - 1) * size; // OFFSET 계산

        List<BoardDto> boardList = boardMapper.getBoardList(offset, size);
        int totalBoardCount = boardMapper.getTotalBoardCount();
        int totalPages = (int) Math.ceil((double) totalBoardCount / size);

        Map<String, Object> response = new HashMap<>();
        response.put("content", boardList);
        response.put("totalPages", totalPages);
        response.put("totalElements", totalBoardCount);
        response.put("size", size);
        response.put("currentPage", page);

        return response;
    }

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

    public List<String> saveBoardFiles(List<MultipartFile> files, String uploadDir){
        return fileUploadUtil.saveFiles(files);
    }
}
