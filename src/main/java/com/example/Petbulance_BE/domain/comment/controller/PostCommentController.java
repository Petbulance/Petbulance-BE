package com.example.Petbulance_BE.domain.comment.controller;

import com.example.Petbulance_BE.domain.comment.dto.request.BulkDeleteCommentReqDto;
import com.example.Petbulance_BE.domain.comment.dto.request.UpdatePostCommentReqDto;
import com.example.Petbulance_BE.domain.comment.dto.response.*;
import com.example.Petbulance_BE.domain.comment.service.PostCommentService;
import com.example.Petbulance_BE.domain.post.type.Topic;
import com.example.Petbulance_BE.global.common.type.AnimalType;
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
    public PostCommentResDto updatePostComment(@PathVariable("commentId") Long commentId,
                                               @RequestBody @Valid UpdatePostCommentReqDto dto) {
        return postCommentService.updatePostComment(commentId, dto);
    }

    @DeleteMapping("/{commentId}")
    public DelCommentResDto deletePostComment(@PathVariable("commentId") Long commentId) {
        return postCommentService.deletePostComment(commentId);
    }

    @DeleteMapping
    public BulkDeleteCommentResDto deleteComments(@RequestBody BulkDeleteCommentReqDto req) {
        return postCommentService.deletePostComments(req.commentIds());
    }


    @GetMapping("/search")
    public SearchPostCommentListResDto searchPostCommentList(@RequestParam(name = "searchKeyword") String searchKeyword,
                                                             @RequestParam(name = "searchScope", defaultValue = "content") String searchScope,
                                                             @RequestParam(name = "lastCommentId", required = false) Long lastCommentId,
                                                             @RequestParam(name = "pageSize", required = false, defaultValue = "20") Integer pageSize,
                                                             @RequestParam(name = "topic", required = false) Topic topic,
                                                             @RequestParam(name = "type", required = false) AnimalType type) {
        return postCommentService.searchPostCommentList(searchKeyword, searchScope, lastCommentId, pageSize, topic, type);
    }

    @GetMapping("/me")
    public PagingMyCommentListResDto myCommentList(@RequestParam(required = false) String keyword,
                                                   @RequestParam(required = false) Long lastCommentId,
                                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        Pageable pageable = PageRequest.of(0, pageSize);
        return postCommentService.myCommentList(keyword, lastCommentId, pageable);
    }
}
