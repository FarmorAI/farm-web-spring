package com.farmorai.backend.mapper;

import com.farmorai.backend.dto.BoardDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BoardMapper {
    List<BoardDto> getBoardList();

    void deleteBoard(Long boardId);

    void insertBoard(BoardDto boardDto);

    void updateBoard(@Param("boardId") Long boardId, @Param("board") BoardDto boardDto);

    BoardDto getBoardDetail(Long boardId);

    void updateBoardViews(Long boardId);
}

