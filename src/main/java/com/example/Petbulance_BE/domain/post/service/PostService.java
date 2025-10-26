package com.example.Petbulance_BE.domain.post.service;

import com.example.Petbulance_BE.domain.board.entity.Board;
import com.example.Petbulance_BE.domain.board.repository.BoardRepository;
import com.example.Petbulance_BE.domain.post.dto.request.CreatePostReqDto;
import com.example.Petbulance_BE.domain.post.dto.response.CreatePostResDto;
import com.example.Petbulance_BE.domain.post.dto.response.InquiryPostResDto;
import com.example.Petbulance_BE.domain.post.entity.Post;
import com.example.Petbulance_BE.domain.post.entity.PostImage;
import com.example.Petbulance_BE.domain.post.repository.PostImageRepository;
import com.example.Petbulance_BE.domain.post.repository.PostRepository;
import com.example.Petbulance_BE.domain.post.repository.PostViewCountRepository;
import com.example.Petbulance_BE.domain.post.type.Category;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import com.example.Petbulance_BE.global.util.UserUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Transactional
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final BoardRepository boardRepository;
    private final PostImageRepository postImageRepository;
    private final PostViewCountRepository postViewCountRepository;

    @Transactional
    public CreatePostResDto createPost(CreatePostReqDto dto) {

        if (dto.getTitle().isBlank() || dto.getContent().isBlank()) {
            throw new CustomException(ErrorCode.EMPTY_TITLE_OR_CONTENT);
        }
        if (dto.getImageUrls() != null && dto.getImageUrls().size() > 10) {
            throw new CustomException(ErrorCode.EXCEEDED_MAX_IMAGE_COUNT);
        }

        Board board = boardRepository.findById(dto.getBoardId())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_BOARD_OR_CATEGORY));

        Category category;
        try {
            category = Category.valueOf(dto.getCategory());
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_BOARD_OR_CATEGORY);
        }

        Post post = Post.builder()
                .board(board)
                .user(UserUtil.getCurrentUser())
                .category(category)
                .title(dto.getTitle())
                .content(dto.getContent())
                .hidden(false)
                .deleted(false)
                .imageNum(Optional.ofNullable(dto.getImageUrls()).orElse(List.of()).size())
                .build();

        Post savedPost = postRepository.save(post);

        savePostImages(savedPost, dto.getImageUrls());

        return CreatePostResDto.of(savedPost, savedPost.getBoard().getId(), dto.getImageUrls());
    }

    private void savePostImages(Post post, List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) return;

        for (int i = 0; i < imageUrls.size(); i++) {
            boolean isThumbnail = (i == 0); // 첫 번째 이미지를 썸네일로 설정
            PostImage postImage = PostImage.create(post, imageUrls.get(i), i+1, isThumbnail);
            postImageRepository.save(postImage);
        }
    }

    public InquiryPostResDto inquiryPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new CustomException(ErrorCode.POST_NOT_FOUND));
        if(post.isHidden()) {
            throw new CustomException(ErrorCode.POST_HIDDEN);
        }
        if(post.isDeleted()) {
            throw new CustomException(ErrorCode.POST_DELETED);
        }
        Users currentUser = UserUtil.getCurrentUser();
        boolean currentUserIsPostAuthor = currentUserIsPostAuthor(post.getUser(), currentUser);

        // 조회수 증가 로직 (비회원 로직 나중에 추가)
        long viewCount = postViewCountRepository.increaseIfNotViewed(postId, currentUser.getId());

        return postRepository.findInquiryPost(post, currentUserIsPostAuthor, currentUser, viewCount);
    }

    private boolean currentUserIsPostAuthor(Users postAuthor, Users currentUser) {
        return UserUtil.getCurrentUser() == postAuthor;
    }
}
