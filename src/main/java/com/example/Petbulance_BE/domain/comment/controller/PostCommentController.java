package com.example.Petbulance_BE.domain.comment.controller;

import com.example.Petbulance_BE.domain.comment.dto.request.UpdatePostCommentReqDto;
import com.example.Petbulance_BE.domain.comment.service.PostCommentService;
import com.example.Petbulance_BE.domain.post.dto.response.PostCommentResDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class PostCommentController {
    private final PostCommentService postCommentService;

    @PatchMapping("/{commentId}")
    public PostCommentResDto updatePostComment(@PathVariable("commentId") Long commentId, @Valid @RequestBody UpdatePostCommentReqDto dto) {
        return postCommentService.updatePostComment(commentId, dto);
    }
}
