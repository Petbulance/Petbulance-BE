package com.example.Petbulance_BE.domain.comment.service;

import com.example.Petbulance_BE.domain.board.repository.BoardRepository;
import com.example.Petbulance_BE.domain.comment.dto.request.UpdatePostCommentReqDto;
import com.example.Petbulance_BE.domain.comment.dto.response.*;
import com.example.Petbulance_BE.domain.comment.entity.PostComment;
import com.example.Petbulance_BE.domain.comment.entity.PostCommentCount;
import com.example.Petbulance_BE.domain.comment.repository.PostCommentCountRepository;
import com.example.Petbulance_BE.domain.comment.repository.PostCommentRepository;
import com.example.Petbulance_BE.domain.post.dto.request.CreatePostCommentReqDto;
import com.example.Petbulance_BE.domain.post.entity.Post;
import com.example.Petbulance_BE.domain.post.repository.PostRepository;
import com.example.Petbulance_BE.domain.post.type.Category;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.domain.user.repository.UsersJpaRepository;
import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import com.example.Petbulance_BE.global.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class PostCommentService {
    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;
    private final UsersJpaRepository usersJpaRepository;
    private final PostCommentCountRepository postCommentCountRepository;
    private final BoardRepository boardRepository;

    @Transactional
    @CacheEvict(
            value = "myComments",
            key = "#currentUser.id + '_0'",
            condition = "#keyword == null"
    )
    public PostCommentResDto createPostComment(Long postId, CreatePostCommentReqDto dto) {
        // parentId와 mentionUserNickname 간의 관계 검증
        if (dto.getParentId() == null && dto.getMentionUserNickname() != null) {
            throw new CustomException(ErrorCode.INVALID_INPUT_RELATION);
        }
        if (dto.getParentId() != null && dto.getMentionUserNickname() == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT_RELATION);
        }

        Post post = findPostById(postId); // 댓글을 작성하는 게시글
        PostComment parentComment = findParentComment(dto.getParentId()); // 댓글이 작성되는 상위댓글 (하위댓글인 경우만)
        Users mentionedUser = findMentionedUser(dto.getMentionUserNickname()); // 답글이 작성되는 경우 멘션된 사용자 (하위댓글인 경우만)
        Users currentUser = UserUtil.getCurrentUser(); // 현재 댓글을 작성하는 사용자

        PostComment saved = postCommentRepository.save(
                PostComment.builder()
                        .post(post)
                        .user(currentUser)
                        .content(dto.getContent())
                        .mentionUser(mentionedUser)
                        .isSecret(dto.getIsSecret())
                        .imageUrl(dto.getImageUrl())
                        .isCommentFromPostAuthor(Objects.equals(post.getUser(), currentUser))
                        .build()
        );

        // 부모댓글이 없으면 자기자신을 부모 댓글로 갖는다. (상위댓글의 경우)
        saved.assignParent(Objects.requireNonNullElse(parentComment, saved));

        // 댓글 수 증가
        int result = postCommentCountRepository.increase(postId);
        if(result == 0) {
            try {
                postCommentCountRepository.save(
                        PostCommentCount.builder()
                                .postId(postId)
                                .postCommentCount(1L)
                                .build()
                );
            } catch (Exception ignored) {}
        }
        return PostCommentResDto.of(saved);
    }

    @CacheEvict(
            value = "myComments",
            key = "#currentUser.id + '_0'",
            condition = "#keyword == null"
    )
    @Transactional(readOnly = true)
    public PostCommentResDto updatePostComment(Long commentId, UpdatePostCommentReqDto dto) {
        if (dto.getContent() == null || dto.getContent().isBlank()) {
            throw new CustomException(ErrorCode.EMPTY_COMMENT_CONTENT);
        }
        PostComment postComment = findPostCommentById(commentId); // 수정하고자하는 댓글
        verifyPostCommentWriter(postComment, Objects.requireNonNull(UserUtil.getCurrentUser())); // 권한 검증

        postComment.update(dto);
        return PostCommentResDto.of(postComment);
    }

    @Transactional
    @CacheEvict(
            value = "myComments",
            key = "#currentUser.id + '_0'",
            condition = "#keyword == null"
    )
    public DelCommentResDto deletePostComment(Long commentId) {
        /* 상위 댓글을 삭제하려는 경우
            (1) 자식댓글이 존재하면 -> deleted를 true로
            (2) 자식댓글이 없는 경우 -> 삭제
         */

        /* 하위 댓글을 삭제하려는 경우
            (1) 상위댓글 삭제여부 true인 경우 + 자식댓글 없는 경우 -> 상위댓글도 연쇄적으로 삭제
            (2) 상위댓글 삭제여부 false인 경우 -> 하위댓글만 삭제
         */
        PostComment postComment = findPostCommentById(commentId); // 삭제하고자하는 댓글
        verifyPostCommentWriter(postComment, Objects.requireNonNull(UserUtil.getCurrentUser()));

        if(!postComment.getDeleted()) { // 아직 삭제되지 않은 댓글
            if(hasChildren(postComment)) { // 자식댓글이 존재하는 경우 (상위댓글)
                // 삭제표시만
                postComment.delete();
                postCommentCountRepository.decrease(postComment.getPost().getId());
            } else {
                delete(postComment); // 삭제 로직
                postCommentCountRepository.decrease(postComment.getPost().getId());
            }
        }
        return new DelCommentResDto("댓글이 성공적으로 삭제되었습니다.");
    }

    private PostComment findPostCommentById(Long commentId) {
        return postCommentRepository.findById(commentId).orElseThrow(
                () -> new CustomException(ErrorCode.COMMENT_NOT_FOUND)
        );
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

    private boolean hasChildren(PostComment postComment) {
        // postComment를 parentComment로 가진 댓글이 하나라도 존재하는지
        return postCommentRepository.countByParent(postComment) > 1;
    }

    // 자식이 없는 경우 -> 댓글 자체를 삭제
    private void delete(PostComment postComment) {
        postCommentRepository.delete(postComment);
        // 하위댓글 -> 상위댓글도 삭제된 상태면 연쇄 삭제
        if(!postComment.isRoot()) {
            postCommentRepository.findById(postComment.getParent().getId())
                    .filter(PostComment::getDeleted) // 상위댓글은 삭제된 상태
                    .filter(Predicate.not(this::hasChildren)) // 상위댓글에 하위댓글도 없는 상태
                    .ifPresent(this::delete);
        }
    }

    private void verifyPostCommentWriter(PostComment postComment, Users currentUser) {
        if (!Objects.equals(postComment.getUser().getId(), currentUser.getId())) {
            throw new CustomException(ErrorCode.FORBIDDEN_COMMENT_ACCESS);
        }
    }

    @Transactional(readOnly = true)
    public PagingPostCommentListResDto postCommentList(Long postId,Long lastParentCommentId, Long lastCommentId, Pageable pageable) {
        Users currentUser = UserUtil.getCurrentUser(); // 현재 댓글을 조회하는 사용자
        Post post = findPostById(postId); // 현재 조회하는 댓글이 달린 게시글

        if(currentUser == null) {
            // 비회원이 게시글 댓글 조회시
            return new PagingPostCommentListResDto(
                    postCommentRepository.findPostCommentByPostForGuest(post, null, null, pageable)
            );
        } else {
            boolean currentUserIsPostAuthor = Objects.equals(currentUser.getId(), post.getUser().getId()); // 현재 사용자가 게시글 작성자인지 -> 이에 따라 조회 가능한 댓글 범위가 달라짐
            return new PagingPostCommentListResDto(
                    postCommentRepository.findPostCommentByPost(post, lastParentCommentId, lastCommentId, pageable, currentUserIsPostAuthor, currentUser)
            );
        }
    }

    @Transactional(readOnly = true)
    public SearchPostCommentListResDto searchPostCommentList(String keyword, String searchScope, Long lastCommentId, Integer pageSize, List<String> category, Long boardId) {
        if(keyword.length() < 2) {
            throw new CustomException(ErrorCode.INVALID_SEARCH_KEYWORD);
        }
        if (!isValidSearchScope(searchScope)) {
            throw new CustomException(ErrorCode.INVALID_SEARCH_SCOPE);
        }
        if (category != null && !category.isEmpty()) {
            for (String cat : category) {
                if (!Category.isValidCategory(cat)) {
                    throw new CustomException(ErrorCode.INVALID_CATEGORY);
                }
            }
        }
        if (boardId != null && !boardRepository.existsById(boardId)) {
            throw new CustomException(ErrorCode.BOARD_NOT_FOUND);
        }

        List<Category> categories = Category.convertToCategoryList(category);
        return new SearchPostCommentListResDto(
                postCommentRepository.findSearchPostComment(keyword, searchScope, lastCommentId, pageSize, categories, boardId),
                postCommentRepository.countSearchPostComment(keyword, searchScope, categories, boardId));
    }

    private boolean isValidSearchScope(String scope) {
        return "writer".equalsIgnoreCase(scope) || "content".equalsIgnoreCase(scope);
    }


    @Transactional(readOnly = true)
    @Cacheable(
            value = "myComments",
            key = "#currentUser.id + '_' + #lastCommentId",
            condition = "#keyword == null",
            unless = "#result == null"
    )
    public PagingMyCommentListResDto myCommentList(String keyword, Long lastCommentId, Pageable pageable) {
        Users currentUser = UserUtil.getCurrentUser();
        return postCommentRepository.findMyCommentList(currentUser, keyword, lastCommentId, pageable);
    }
}
