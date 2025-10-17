package com.example.Petbulance_BE.domain.post.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreatePostCommentReqDto {
    @NotBlank(message = "댓글 내용은 필수입니다.")
    private String content;

    private Long parentId;

    private String mentionUserNickname;

    private String imageUrl;

    @NotNull(message = "비밀댓글 여부(isSecret)는 필수입니다.")
    private Boolean isSecret;

    @AssertTrue(message = "parentId가 null이면 mentionUserNickname도 null이어야 하며, parentId가 null이 아니면 mentionUserNickname도 null이 아니어야 합니다.")
    public boolean isMentionUserValid() {
        if (parentId == null) {
            return mentionUserNickname == null;
        } else {
            return mentionUserNickname != null;
        }
    }
}
