package com.farmorai.backend.mapper;

import com.farmorai.backend.dto.BoardDto;
import com.farmorai.backend.dto.PageRequestDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BoardMapper {
    List<BoardDto> getBoardList(PageRequestDto pageRequestDto);
    int getBoardListCount(PageRequestDto pageRequestDto);

    void deleteBoard(Long boardId);

    void insertBoard(@Param("memberId") Long memberId, @Param("board") BoardDto boardDto);

    void updateBoard(@Param("boardId") Long boardId, @Param("board") BoardDto boardDto);

    BoardDto getBoardDetail(Long boardId);

    void updateBoardViews(Long boardId);
}

