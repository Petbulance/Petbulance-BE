package com.example.Petbulance_BE.domain.post.service;

import com.example.Petbulance_BE.domain.board.entity.Board;
import com.example.Petbulance_BE.domain.board.repository.BoardRepository;
import com.example.Petbulance_BE.domain.comment.entity.PostComment;
import com.example.Petbulance_BE.domain.comment.repository.PostCommentRepository;
import com.example.Petbulance_BE.domain.post.dto.request.CreatePostCommentReqDto;
import com.example.Petbulance_BE.domain.post.dto.request.CreatePostReqDto;
import com.example.Petbulance_BE.domain.post.dto.response.PostCommentResDto;
import com.example.Petbulance_BE.domain.post.entity.Post;
import com.example.Petbulance_BE.domain.post.entity.PostImage;
import com.example.Petbulance_BE.domain.post.repository.PostImageRepository;
import com.example.Petbulance_BE.domain.post.repository.PostRepository;
import com.example.Petbulance_BE.domain.post.type.Category;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.domain.user.repository.UsersJpaRepository;
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
    private final PostCommentRepository postCommentRepository;
    private final UsersJpaRepository usersJpaRepository;

    @Transactional
    public Post createPost(CreatePostReqDto dto) {

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

        return savedPost;
    }

    private void savePostImages(Post post, List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) return;

        for (int i = 0; i < imageUrls.size(); i++) {
            boolean isThumbnail = (i == 0); // 첫 번째 이미지를 썸네일로 설정
            PostImage postImage = PostImage.create(post, imageUrls.get(i), i+1, isThumbnail);
            postImageRepository.save(postImage);
        }
    }

    @Transactional
    public PostCommentResDto createPostComment(Long postId, CreatePostCommentReqDto dto) {
        validateCommentContent(dto.getContent());

        Post post = findPostById(postId);
        PostComment parentComment = findParentComment(dto.getParentId());
        Users mentionedUser = findMentionedUser(dto.getMentionUserNickname());

        PostComment newComment = buildPostComment(post, parentComment, mentionedUser, dto);
        PostComment saved = postCommentRepository.save(newComment);

        return PostCommentResDto.of(saved);
    }

    private void validateCommentContent(String content) {
        if (content == null || content.isBlank()) {
            throw new CustomException(ErrorCode.EMPTY_COMMENT_CONTENT);
        }
    }

    private Post findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
    }

    private PostComment findParentComment(Long parentId) {
        if (parentId == null) return null;
        return postCommentRepository.findById(parentId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_PARENT_COMMENT));
    }

    private Users findMentionedUser(String nickname) {
        if (nickname == null || nickname.isBlank()) return null;
        return usersJpaRepository.findByNickname(nickname)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_MENTION_USER));
    }

    private PostComment buildPostComment(Post post, PostComment parent, Users mentionedUser, CreatePostCommentReqDto dto) {
        return PostComment.builder()
                .post(post)
                .user(UserUtil.getCurrentUser())
                .content(dto.getContent())
                .parent(parent)
                .mentionUser(mentionedUser)
                .isSecret(dto.isSecret())
                .imageUrl(dto.getImageUrl())
                .build();
    }

}
