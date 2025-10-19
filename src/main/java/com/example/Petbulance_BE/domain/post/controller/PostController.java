package com.example.Petbulance_BE.domain.post.controller;

import com.example.Petbulance_BE.domain.comment.dto.response.PostCommentResDto;
import com.example.Petbulance_BE.domain.comment.service.PostCommentService;
import com.example.Petbulance_BE.domain.post.dto.PostLikeDto;
import com.example.Petbulance_BE.domain.post.dto.request.CreatePostCommentReqDto;
import com.example.Petbulance_BE.domain.post.dto.request.CreatePostReqDto;
import com.example.Petbulance_BE.domain.post.dto.response.CreatePostResDto;
import com.example.Petbulance_BE.domain.comment.dto.response.PostCommentListResDto;
import com.example.Petbulance_BE.domain.post.service.PostLikeService;
import com.example.Petbulance_BE.domain.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;
    private final PostLikeService postLikeService;
    private final PostCommentService postCommentService;

    @PostMapping
    public CreatePostResDto createPost(@Valid @RequestBody CreatePostReqDto dto) {
        return postService.createPost(dto);
    }

    @PostMapping("/{postId}/likes")
    public PostLikeDto postLike(@PathVariable("postId") Long postId){
        return postLikeService.postLike(postId);
    }

    @DeleteMapping("/{postId}/likes")
    public PostLikeDto postUnlike(@PathVariable("postId") Long postId){
        return postLikeService.postUnlike(postId);
    }

    @PostMapping("/{postId}/comments")
    public PostCommentResDto createPostComment(@PathVariable("postId") Long postId, @Valid @RequestBody CreatePostCommentReqDto dto) {
        return postCommentService.createPostComment(postId, dto);
    }

    @GetMapping("/{postId}/comments")
    public Slice<PostCommentListResDto> postCommentList(@PathVariable("postId") Long postId,
                                                        @RequestParam(required = false) Long lastParentCommentId,
                                                        @RequestParam(required = false) Long lastCommentId,
                                                        @RequestParam(defaultValue = "15") int pageSize) {
        Pageable pageable = PageRequest.of(0, pageSize);
        return postCommentService.postCommentList(postId, lastParentCommentId, lastCommentId, pageable);
    }
}
