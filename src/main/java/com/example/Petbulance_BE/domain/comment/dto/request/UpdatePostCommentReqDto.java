package com.example.Petbulance_BE.domain.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePostCommentReqDto {
    @NotBlank(message = "댓글 내용은 필수입니다.")
    private String content;
    private String imageUrl;
    @NotNull(message = "비밀댓글 여부(isSecret)는 필수입니다.")
    private boolean isSecret;
}
