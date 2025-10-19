package com.example.Petbulance_BE.domain.comment.dto.response;

import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.global.util.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostCommentListResDto {
    private Boolean isRoot;
    private Long commentId;
    private Long parentId;
    private String writerNickname;
    private String writerProfileUrl;
    private String mentionUserNickname;
    private String content;
    private Boolean isSecret;
    private Boolean isPostAuthor;     // 게시글 작성자
    private Boolean isCommentAuthor;  // 댓글 작성자
    private Boolean deleted;
    private Boolean hidden;
    private String imageUrl;
    private Boolean visibleToUser;
    private String createdAt;

    public static PostCommentListResDto of(PostCommentListSubDto subDto,
                                           boolean isPostAuthor,
                                           Users currentUser) {
        PostCommentListResDto resDto = new PostCommentListResDto();
        // parentId 가 null일 수 있으면 null-safe 비교
        resDto.isRoot = subDto.getParentId() != null
                && Objects.equals(subDto.getCommentId(), subDto.getParentId());

        resDto.commentId = subDto.getCommentId();
        resDto.parentId = subDto.getParentId();
        resDto.writerNickname = subDto.getWriterNickname();
        resDto.writerProfileUrl = subDto.getWriterProfileUrl();
        resDto.mentionUserNickname = subDto.getMentionUserNickname();
        resDto.content = subDto.getContent();
        resDto.isSecret = subDto.isSecret();
        resDto.isPostAuthor = isPostAuthor;

        // Users.id 가 String(UUID) 이므로 SubDto의 writerId 타입도 String 이어야 함
        resDto.isCommentAuthor = Objects.equals(subDto.getWriterId(), currentUser.getId());

        resDto.deleted = subDto.isDeleted();
        resDto.hidden = subDto.isHidden();
        resDto.imageUrl = subDto.getImageUrl();

        // 표시 가시성 결정 (resDto 인스턴스로 호출해야 함)
        if (isPostAuthor) {
            resDto.setWriterVisibleComment();
        } else {
            resDto.setViewerVisibleComment(currentUser.getNickname());
        }

        // createdAt 포맷팅
        resDto.createdAt = TimeUtil.formatCreatedAt(subDto.getCreatedAt());

        return resDto;
    }

    /** 게시글 작성자 시점에서 댓글 표시 여부 결정 */
    public void setWriterVisibleComment() {
        this.visibleToUser = isVisibleToWriter();
        if (!this.visibleToUser) {
            this.writerNickname = null;
            this.writerProfileUrl = null;
            this.content = null;
            this.imageUrl = null;
        }
    }

    private boolean isVisibleToWriter() {
        // 삭제되거나 숨김된 댓글은 작성자도 볼 수 없음
        return !Boolean.TRUE.equals(deleted) && !Boolean.TRUE.equals(hidden);
    }

    /** 특정 사용자(currentUser) 기준에서 댓글 표시 여부 결정 */
    public void setViewerVisibleComment(String currentUser) {
        this.visibleToUser = this.isViewerVisibility(currentUser);
        if (!this.visibleToUser) {
            this.writerNickname = null;
            this.writerProfileUrl = null;
            this.content = null;
            this.imageUrl = null;
        }
    }

    private boolean isViewerVisibility(String currentUser) {
        if (Boolean.TRUE.equals(deleted) || Boolean.TRUE.equals(hidden)) return false;
        if (Boolean.TRUE.equals(isSecret)) {
            return Boolean.TRUE.equals(isCommentAuthor)
                    || (mentionUserNickname != null && mentionUserNickname.equals(currentUser));
        }
        return true;
    }
}
