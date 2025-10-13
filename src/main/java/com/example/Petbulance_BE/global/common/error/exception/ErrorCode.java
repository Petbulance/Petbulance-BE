package com.example.Petbulance_BE.global.common.error.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    TEST_ERROR_CODE(HttpStatus.BAD_REQUEST, "오류가 발생하였습니다."),
    EMPTY_TITLE_OR_CONTENT(HttpStatus.BAD_REQUEST, "제목과 본문은 비워둘 수 없습니다."),
    EXCEEDED_MAX_IMAGE_COUNT(HttpStatus.BAD_REQUEST, "이미지는 최대 10장까지만 첨부할 수 있습니다."),
    INVALID_BOARD_OR_CATEGORY(HttpStatus.BAD_REQUEST, "유효하지 않은 게시판 또는 카테고리입니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "요청하신 게시글을 찾을 수 없습니다."),
    ALREADY_LIKED(HttpStatus.BAD_REQUEST, "이미 좋아요를 누른 게시글입니다."),
    NOT_LIKED_YET(HttpStatus.BAD_REQUEST, "아직 좋아요를 누르지 않은 게시글입니다."),
    LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "좋아요 내역이 존재하지 않습니다."),
    EMPTY_COMMENT_CONTENT(HttpStatus.BAD_REQUEST, "댓글 내용을 입력해주세요." ),
    INVALID_PARENT_COMMENT(HttpStatus.BAD_REQUEST, "상위 댓글 정보를 찾을 수 없습니다"),
    INVALID_MENTION_USER(HttpStatus.BAD_REQUEST, "멘션된 사용자 정보를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;
}
