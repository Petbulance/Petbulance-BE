package com.example.Petbulance_BE.domain.comment.controller;

import com.example.Petbulance_BE.domain.comment.dto.request.UpdatePostCommentReqDto;
import com.example.Petbulance_BE.domain.comment.dto.response.DelCommentResDto;
import com.example.Petbulance_BE.domain.comment.dto.response.PagingMyCommentListResDto;
import com.example.Petbulance_BE.domain.comment.dto.response.PostCommentResDto;
import com.example.Petbulance_BE.domain.comment.dto.response.SearchPostCommentListResDto;
import com.example.Petbulance_BE.domain.comment.service.PostCommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class PostCommentController {
    private final PostCommentService postCommentService;

    @PatchMapping("/{commentId}")
    public PostCommentResDto updatePostComment(@PathVariable("commentId") Long commentId, @Valid @RequestBody UpdatePostCommentReqDto dto) {
        return postCommentService.updatePostComment(commentId, dto);
    }

    @DeleteMapping("/{commentId}")
    public DelCommentResDto deletePostComment(@PathVariable("commentId") Long commentId) {
        return postCommentService.deletePostComment(commentId);
    }

    @GetMapping("/search")
    public SearchPostCommentListResDto searchPostComment(@RequestParam(name = "keyword", required = true) String keyword,
                                                         @RequestParam(name = "searchScope", required = true, defaultValue = "content") String searchScope,
                                                         @RequestParam(name = "lastCommentId", required = false) Long lastCommentId,
                                                         @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                                                         @RequestParam(name = "category", required = false) List<String> category,
                                                         @RequestParam(name = "boardId", required = false) Long boardId) {
        return postCommentService.searchPostComment(keyword, searchScope, lastCommentId, pageSize, category, boardId);
    }

    @GetMapping("/me")
    public PagingMyCommentListResDto myCommentList(@RequestParam(required = false) String keyword, @RequestParam(required = false) Long lastCommentId, @RequestParam(defaultValue = "10") Integer pageSize) {
        Pageable pageable = PageRequest.of(0, pageSize);
        return postCommentService.myCommentList(keyword, lastCommentId, pageable);
    }
}
