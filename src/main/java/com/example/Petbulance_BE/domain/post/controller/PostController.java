package com.example.Petbulance_BE.domain.post.controller;

import com.example.Petbulance_BE.domain.comment.dto.response.PagingPostCommentListResDto;
import com.example.Petbulance_BE.domain.comment.dto.response.PostCommentResDto;
import com.example.Petbulance_BE.domain.comment.service.PostCommentService;
import com.example.Petbulance_BE.domain.post.dto.request.BulkDeletePostReqDto;
import com.example.Petbulance_BE.domain.post.dto.response.PagingMyPostListResDto;
import com.example.Petbulance_BE.domain.post.dto.PostLikeDto;
import com.example.Petbulance_BE.domain.post.dto.request.CreatePostCommentReqDto;
import com.example.Petbulance_BE.domain.post.dto.request.CreatePostReqDto;
import com.example.Petbulance_BE.domain.post.dto.request.UpdatePostReqDto;
import com.example.Petbulance_BE.domain.post.dto.response.*;
import com.example.Petbulance_BE.domain.post.service.PostLikeService;
import com.example.Petbulance_BE.domain.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;
    private final PostLikeService postLikeService;
    private final PostCommentService postCommentService;

    @PostMapping
    public CreatePostResDto createPost(@RequestBody @Valid CreatePostReqDto dto) {
        return postService.createPost(dto);
    }

    @PutMapping("/{postId}")
    public UpdatePostResDto updatePost(@PathVariable("postId") Long postId,
                                       @RequestBody UpdatePostReqDto dto) {
        return postService.updatePost(postId, dto);
    }

    @DeleteMapping("/{postId}")
    public DeletePostResDto deletePost(@PathVariable("postId") Long postId) {
        return postService.deletePost(postId);
    }

    @DeleteMapping
    public BulkDeletePostResDto deletePosts(@RequestBody BulkDeletePostReqDto request) {
        return postService.deletePosts(request.postIds());
    }


    @GetMapping("/{postId}")
    public DetailPostResDto detailPost(@PathVariable Long postId) {
        return postService.detailPost(postId);
    }

    @GetMapping
    public PagingPostListResDto postList(@RequestParam(required = false) Long boardId,
                                         @RequestParam(required = false) String category,
                                         @RequestParam(defaultValue = "popular") String sort,
                                         @RequestParam(required = false) Long lastPostId,
                                         @RequestParam(defaultValue = "10") Integer pageSize) {
        return postService.postList(boardId, category, sort, lastPostId, pageSize);
    }

    @GetMapping("/search")
    public PagingPostSearchListResDto postSearchList(@RequestParam(required = false) Long boardId,
                                                     @RequestParam(required = false) List<String> category,
                                                     @RequestParam(defaultValue = "popular") String sort,
                                                     @RequestParam(required = false) Long lastPostId,
                                                     @RequestParam(defaultValue = "10") Integer pageSize,
                                                     @RequestParam(required = false) String searchKeyword,
                                                     @RequestParam(defaultValue = "title_content") String searchScope) {
        return postService.postSearchList(boardId, category, sort, lastPostId, pageSize, searchKeyword, searchScope);
    }

    @GetMapping("/me")
    public PagingMyPostListResDto myPostList(@RequestParam(required = false) String keyword,
                                             @RequestParam(required = false) Long lastPostId,
                                             @RequestParam(defaultValue = "10") Integer pageSize) {
        Pageable pageable = PageRequest.of(0, pageSize);
        return postService.myPostList(keyword, lastPostId, pageable);
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
    public PostCommentResDto createPostComment(@PathVariable("postId") Long postId,
                                               @RequestBody @Valid CreatePostCommentReqDto dto) {
        return postCommentService.createPostComment(postId, dto);
    }

    @GetMapping("/{postId}/comments")
    public PagingPostCommentListResDto postCommentList(@PathVariable("postId") Long postId,
                                                       @RequestParam(required = false) Long lastParentCommentId,
                                                       @RequestParam(required = false) Long lastCommentId,
                                                       @RequestParam(defaultValue = "15") int pageSize) {
        Pageable pageable = PageRequest.of(0, pageSize);
        return postCommentService.postCommentList(postId, lastParentCommentId, lastCommentId, pageable);
    }
}
