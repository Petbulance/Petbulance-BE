package com.example.Petbulance_BE.domain.notification.type;

public enum NotificationType {
    // <커뮤니티>
    POST_COMMENT,     // 누군가 내 글에 댓글
    COMMENT_REPLY,    // 누군가 내 댓글에 답글
    POST_LIKE,        // 누군가 내 글에 좋아요

    // <후기>
    REVIEW_HELPFUL,   // 누군가 내 후기에 도움이 됐어요

    // <신고/제재>
    POST_DELETED,     // 경고: 게시글 삭제
    TEMP_BAN_7D,      // 7일 정지
    PERMANENT_BAN     // 영구 정지
}
