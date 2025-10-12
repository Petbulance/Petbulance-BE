package com.example.Petbulance_BE.domain.post.controller;

import com.example.Petbulance_BE.domain.post.dto.PostLikeDto;
import com.example.Petbulance_BE.domain.post.dto.request.CreatePostReqDto;
import com.example.Petbulance_BE.domain.post.entity.Post;
import com.example.Petbulance_BE.domain.post.service.PostLikeService;
import com.example.Petbulance_BE.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;
    private final PostLikeService postLikeService;

    @PostMapping
    public Post createPost(@RequestBody CreatePostReqDto dto) {
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
}
