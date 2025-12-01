package com.example.Petbulance_BE.global.common.error.exception;

import com.google.api.Http;
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
    LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "좋아요 내역이 존재하지 않습니다."),
    EMPTY_COMMENT_CONTENT(HttpStatus.BAD_REQUEST, "댓글 내용을 입력해주세요." ),
    INVALID_PARENT_COMMENT(HttpStatus.BAD_REQUEST, "상위 댓글 정보를 찾을 수 없습니다"),
    INVALID_MENTION_USER(HttpStatus.BAD_REQUEST, "멘션된 사용자 정보를 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "요청하신 댓글을 찾을 수 없습니다."),
    UNAUTHORIZED_USER(HttpStatus.BAD_REQUEST, "블랙리스트에 등록된 액세스 토큰 접근이 제한되었습니다."),
    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "액세스 토큰이 만료되었습니다."),
    NON_EXIST_REFRESH_TOKEN(HttpStatus.BAD_REQUEST,"리프레시 토큰이 존재하지 않습니다"),
    INVALID_PROVIDER(HttpStatus.BAD_REQUEST, "프로바이더가 유효하지 않습니다."),
    FirebaseToken_Fail(HttpStatus.BAD_REQUEST, "파이어베이스 커스텀 토큰 생성에 실패하였습니다."),
    NON_EXIST_USER(HttpStatus.BAD_REQUEST, "존재하지 않는 유저입니다."),
    SNS_ACCOUNT_ALREADY_LINKED(HttpStatus.BAD_REQUEST, "해당 소셜 계정은 이미 다른 계정과 연결되어 있습니다."),
    FAIL_KAKAO_LOGIN(HttpStatus.BAD_REQUEST,"카카오 로그인 실패"),
    FAIL_NAVER_LOGIN(HttpStatus.BAD_REQUEST,"네이버 로그인 실패"),
    FAIL_GOOGLE_LOGIN(HttpStatus.BAD_REQUEST, "구글 로그인 실패"),
    CANNOT_DISCONNECT_LAST_LOGIN_METHOD(HttpStatus.BAD_REQUEST,"유일한 로그인 수단은 해제할 수 없습니다."),
    FAIL_IMAGE_UPLOAD(HttpStatus.BAD_REQUEST,"이미지 업로드에 실패하였습니다."),
    INVALID_INPUT_RELATION(HttpStatus.BAD_REQUEST, "입력 관계가 잘못되었습니다."),
    FORBIDDEN_LIKE_ACCESS(HttpStatus.UNAUTHORIZED, "좋아요에 대한 권한이 존재하지 않습니다."),
    FORBIDDEN_COMMENT_ACCESS(HttpStatus.UNAUTHORIZED, "댓글에 대한 권한이 존재하지 않습니다."),
    FORBIDDEN_POST_ACCESS(HttpStatus.UNAUTHORIZED, "게시글에 대한 권한이 존재하지 않습니다."),
    INVALID_SEARCH_KEYWORD(HttpStatus.BAD_REQUEST, "검색어는 2글자 이상이어야 합니다."),
    INVALID_SEARCH_SCOPE(HttpStatus.BAD_REQUEST, "유효하지 않은 검색 범위값 입니다."),
    INVALID_CATEGORY(HttpStatus.BAD_REQUEST, "유효하지 않은 category 값입니다."),
    BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 게시판입니다."),
    ACCOUNT_SUSPENDED(HttpStatus.BAD_REQUEST,"이용이 정지된 계정입니다. 고객센터에 문의해 주세요."),
    NOT_FOUND_APP_VERSION(HttpStatus.BAD_REQUEST, "앱 버전 정보가 존재하지 않습니다."),
    NOT_FOUND_HOSPITAL(HttpStatus.BAD_REQUEST, "요청하신 병원을 찾을 수 없습니다."),
    POST_HIDDEN(HttpStatus.FORBIDDEN, "숨겨진 게시글입니다."),
    POST_DELETED(HttpStatus.GONE, "삭제된 게시글입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.BAD_REQUEST, "게시글 조회 중 오류가 발생했습니다."),
    INVALID_SORT_CONDITION(HttpStatus.BAD_REQUEST, "유효하지 않은 정렬 조건입니다."),
    NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND, "요청하신 공지사항을 찾을 수 없습니다."),
    EMPTY_QNA_CONTENT(HttpStatus.BAD_REQUEST, "문의 제목 또는 내용을 입력해주세요."),
    FORBIDDEN_QNA_ACCESS(HttpStatus.UNAUTHORIZED, "qna에 대한 접근 권한이 없습니다."),
    QNA_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 qnaId의 문의글을 찾을 수 없습니다."),
    PRIVACY_CONSENT_REQUIRED(HttpStatus.BAD_REQUEST, "개인정보 수집 및 이용 동의가 필요합니다."),
    INVALID_CONTACT_INFO(HttpStatus.BAD_REQUEST, "연락처는 최소 1개 이상 입력해야 합니다."),
    INVALID_TYPE(HttpStatus.BAD_REQUEST,"올바르지 않은 type값입니다."),
    INQUIRY_NOT_FOUND(HttpStatus.NOT_FOUND, "요청하신 문의내역을 찾을 수 없습니다."),
    FORBIDDEN_INQUIRY_ACCESS(HttpStatus.UNAUTHORIZED, "문의내역에 대한 권한이 존재하지 않습니다."),
    NOT_FOUND_KEYWORD(HttpStatus.BAD_REQUEST, "존재하지 않는 키워드 입니다."),
    FAIL_READ_RANDOM_NICKNAME_FILE(HttpStatus.BAD_REQUEST, "랜덤 닉네임 조합 파일을 읽는데 실패하였습니다"),
    NOT_FOUND_RECEIPT(HttpStatus.BAD_REQUEST, "영수증 이미지가 존재하지 않습니다."),
    FAIL_RECEIPT_EXTRACT(HttpStatus.BAD_REQUEST, "영수증 정보 추출에 실패하였습니다."),
    FAIL_API_CONNECT(HttpStatus.BAD_REQUEST, "외부 API연결에 실패하였습니다."),
    FAIL_WHILE_API(HttpStatus.BAD_REQUEST, "외부 API요청 도중 예외가 발생하였습니다."),
    NO_ADDRESS_FOUND(HttpStatus.BAD_REQUEST, "영수증에서 주소 정보를 얻지 못하였습니다."),
    FAIL_GEOCODING(HttpStatus.BAD_REQUEST, "지오코딩에 실패하였습니다."),
    NOT_FOUND_RECEIPT_HOSPITAL(HttpStatus.BAD_REQUEST, "영수증에 존재하는 병원을 찾지 못했습니다."),
    IMAGE_COUNT_EXCEEDED(HttpStatus.BAD_REQUEST, "최대 5장까지 등록할 수 있습니다."),
    NOT_FOUND_REVIEW(HttpStatus.BAD_REQUEST, "리뷰를 찾을 수 없습니다."),
    FAIL_DELETE_IMAGE(HttpStatus.BAD_REQUEST, "이미지 삭제에 실패했습니다"),
    IMAGE_PROCESSING_ERROR(HttpStatus.BAD_REQUEST, "이미지 처리 중 오류가 발생했습니다."),
    GEMINI_API_CONNECTION_ERROR(HttpStatus.BAD_REQUEST, "AI 서버와의 통신이 원활하지 않습니다."),
    AI_RESPONSE_PARSING_ERROR(HttpStatus.BAD_REQUEST, "AI 응답을 분석하는 데 실패했습니다."),
    AI_DIAGNOSIS_FAIL(HttpStatus.BAD_REQUEST, "AI 진단에 실패했습니다."),
    BAD_IMAGE(HttpStatus.BAD_REQUEST, "이미지가 손상되었거나 동물이 아닙니다.");

    private final HttpStatus status;
    private final String message;
}
