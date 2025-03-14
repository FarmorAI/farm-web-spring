package com.farmorai.backend.service;

import com.farmorai.backend.dto.CommentDto;
import com.farmorai.backend.mapper.CommentMapper;
import com.farmorai.backend.securityFilter.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {
    private final CommentMapper commentMapper;

    // 댓글 리스트 조회
    public List<CommentDto> getCommentList(Long boardId) {
        // 첫 번째 단계: SQL에서 parent_id, depth로 이미 정렬된 댓글 목록을 가져옵니다.
        List<CommentDto> comments = commentMapper.getCommentList(boardId);

        // 두 번째 단계: 부모 댓글 뒤에 자식 댓글이 오도록 재정렬
        List<CommentDto> sortedComments = new ArrayList<>();

        // 부모 댓글을 먼저 처리하고, 그 뒤에 자식 댓글을 삽입합니다.
        for (CommentDto comment : comments) {
            if (comment.getParentId() == null) { // 부모 댓글을 찾음
                // 부모 댓글은 먼저 추가
                sortedComments.add(comment);
                // 자식 댓글들을 해당 부모 댓글 뒤에 삽입
                insertChildren(comment, comments, sortedComments);
            }
        }

        return sortedComments;
    }

    private void insertChildren(CommentDto parent, List<CommentDto> comments, List<CommentDto> sortedComments) {
        // 부모 댓글에 대한 자식 댓글들을 찾고, 그 자식 댓글들을 적절히 추가
        List<CommentDto> children = new ArrayList<>();
        for (CommentDto comment : comments) {
            if (parent.getCommentId().equals(comment.getParentId())) {
                children.add(comment);
            }
        }

        // 자식 댓글들을 depth가 낮은 순서대로 정렬
        children.sort(Comparator.comparingInt(CommentDto::getDepth));

        // 자식 댓글들을 부모 댓글 뒤에 추가
        for (CommentDto child : children) {
            sortedComments.add(child);
            // 자식 댓글에 대해서도 재귀적으로 자식 댓글을 추가
            insertChildren(child, comments, sortedComments);
        }
    }

    // 댓글 추가
    @Transactional
    public void insertComment(CommentDto commentDto) {
        if (commentDto.getMemberId() == null) {
            throw new IllegalArgumentException("Member ID가 누락되었습니다. 로그인 상태를 확인하세요.");
        }
        if (commentDto.getParentId() == null) { // 원댓글이면 ref가 null
            // 원댓글 기본 값 설정
            commentDto.setDepth(0);
            commentDto.setLevel(0);
            commentDto.setRef(0L);
            commentMapper.insertComment(commentDto);
            // 삽입 후 자기 자신을 ref로 설정
            commentDto.setRef(commentDto.getCommentId());
            commentMapper.updateCommentRef(commentDto.getCommentId());
        } else { // 대댓글
            CommentDto parent = commentMapper.getCommentById(commentDto.getParentId());
            if (parent == null) {
                throw new IllegalArgumentException("부모 댓글이 존재하지 않습니다. parentId 값: " + commentDto.getParentId());
            }
            // 같은 ref 내에서 가장 큰 depth 값을 찾고, 그 값 + 1로 설정
            int maxDepth = commentMapper.getMaxDepthByRef(parent.getRef());
            commentDto.setDepth(maxDepth + 1);
            // 부모댓글의 level 값에 +1로 설정
            commentDto.setLevel(parent.getLevel() + 1);
            commentDto.setRef(parent.getRef());
            commentMapper.insertComment(commentDto);
        }
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        try {
            CommentDto comment = commentMapper.getCommentById(commentId);


            // 댓글 작성자 ID 가져오기 (CommentDto 기준)
            Long commentOwnerId = comment.getMemberId(); // CommentDto에서 userId 가져오기

            // 댓글 작성자가 현재 사용자와 일치하는지 확인
            if (!commentOwnerId.equals(userId)) {
                throw new AccessDeniedException("본인이 작성한 댓글만 삭제할 수 있습니다.");
            }

            // 대댓글이 있는 경우 → 소프트 삭제
            if (commentMapper.hasChildComments(commentId)) {
                commentMapper.markCommentAsDeleted(commentId);
            } else { // 대댓글이 없는 경우 → 실제 삭제
                commentMapper.deleteComment(commentId);
            }
        } catch (Exception e) {
            throw new RuntimeException("댓글 삭제 중 오류 발생: " + e.getMessage());
        }
    }


    // 댓글 수정
    @Transactional
    public void updateComment(Long commentId, CommentDto commentDto) {
        commentMapper.updateComment(commentId, commentDto);
    }
}
