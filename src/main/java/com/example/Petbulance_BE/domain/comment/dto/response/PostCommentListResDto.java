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
    private Boolean isCommentFromPostAuthor;   // 현재 댓글이 게시글 작성자의 댓글인지
    private Boolean isCommentAuthor;  // 현재 사용자가 작성한 댓글인지?
    private Boolean deleted;
    private Boolean hidden;
    private String imageUrl;
    private Boolean visibleToUser;
    private String createdAt;


    public static PostCommentListResDto of(PostCommentListSubDto subDto,
                                           boolean currentUserIsPostAuthor,
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
        resDto.isCommentFromPostAuthor = subDto.isCommentFromPostAuthor();
        resDto.isCommentAuthor = Objects.equals(subDto.getWriterId(), currentUser.getId()); // 댓글 작성자와 현재 사용자가 같은지

        resDto.deleted = subDto.isDeleted();
        resDto.hidden = subDto.isHidden();
        resDto.imageUrl = subDto.getImageUrl();

        // 표시 가시성 결정 (resDto 인스턴스로 호출해야 함)
        if (currentUserIsPostAuthor) {
            resDto.setWriterVisibleComment(); // 게시글 작성자가 볼 수 있는 댓글인지 검증
        } else {
            resDto.setViewerVisibleComment(currentUser.getNickname()); // 게시글 조회자가 볼 수 있는 댓글인지 검증
        }

        if (resDto.visibleToUser) {
            resDto.createdAt = TimeUtil.formatCreatedAt(subDto.getCreatedAt());
        } else {
            resDto.createdAt = null;
        }
        return resDto;
    }

    /** 게시글 작성자 시점에서 댓글 표시 여부 결정 */
    public void setWriterVisibleComment() {
        this.visibleToUser = isVisibleToWriter();
        if (!this.visibleToUser) { // 볼 수 없는 댓글이면 -> 작성자 닉네임, 작성자 프로필, 댓글 내용, 댓글 이미지 볼 수 없음
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
        if (Boolean.TRUE.equals(deleted) || Boolean.TRUE.equals(hidden)) return false; // 삭제 또는 숨김 댓글은 볼 수 없음
        if (Boolean.TRUE.equals(isSecret)) { // 비밀 댓글인데
            return Boolean.TRUE.equals(isCommentAuthor) // 댓글 작성자이거나 멘션된 사용자면 볼 수 있음
                    || (mentionUserNickname != null && mentionUserNickname.equals(currentUser));
        }
        return true;
    }
}
