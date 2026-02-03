package com.example.Petbulance_BE.domain.admin.user.controller;

import com.example.Petbulance_BE.domain.admin.page.PageResponse;
import com.example.Petbulance_BE.domain.admin.user.dto.GetUserQueryParam;
import com.example.Petbulance_BE.domain.admin.user.dto.GetUsersResDto;
import com.example.Petbulance_BE.domain.admin.user.dto.OneUserResDto;
import com.example.Petbulance_BE.domain.admin.user.service.AdminUserService;
import com.example.Petbulance_BE.domain.adminlog.aop.AdminLoggable;
import com.example.Petbulance_BE.domain.adminlog.type.AdminActionType;
import com.example.Petbulance_BE.domain.adminlog.type.AdminPageType;
import com.example.Petbulance_BE.domain.adminlog.type.AdminTargetType;
import com.example.Petbulance_BE.domain.report.service.CommunitySanctionService;
import com.example.Petbulance_BE.domain.user.type.SanctionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor
@Slf4j
public class AdminUserController {

    private final AdminUserService adminUserService;
    private final CommunitySanctionService communitySanctionService;

    @GetMapping("/search")
    public PageResponse<GetUsersResDto> getUsers(@PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
                                                 @ModelAttribute GetUserQueryParam queryParam) {
        return adminUserService.getUsersProcess(pageable, queryParam);

    }

    @GetMapping("/{userID}")
    public OneUserResDto detailuser(@PathVariable String userID) {

        return adminUserService.getDetailUser(userID);

    }

    @AdminLoggable(
            pageType = AdminPageType.USER_MANAGEMENT,
            actionType = AdminActionType.CREATE,
            targetType = AdminTargetType.REVIEW_ACTION,
            targetId = "#reportID",
            description = "유저 리뷰 정지 조치(타겟 ID는 정지 근거 신고ID)"
    )
    @PatchMapping("/reviewBan/{reportId}")
    public Map<String, String> reviewBan(@PathVariable Long reportId) {

        adminUserService.banUserReviewProcess(reportId, SanctionType.REVIEW_BAN);

        return Map.of("message", "success");

    }

    @AdminLoggable(
            pageType = AdminPageType.USER_MANAGEMENT,
            actionType = AdminActionType.UPDATE,
            targetType = AdminTargetType.REVIEW_ACTION,
            targetId = "#userID",
            description = "유저 리뷰 정지 취소"
    )
    @PatchMapping("/reactive/review/{userId}")
    public Map<String,String> reactiveReview(@PathVariable String userId) {

        adminUserService.reactiveReviewProcess(userId);

        return Map.of("message", "success");

    }

    @AdminLoggable(
            pageType = AdminPageType.USER_MANAGEMENT,
            actionType = AdminActionType.DELETE,
            targetType = AdminTargetType.USER_INFO,
            targetId = "#userID",
            description = "유저 삭제(30일 보관후 자동 삭제 처리)"
    )
    @DeleteMapping("/delete/{userID}")
    public String deleteUser(@PathVariable String userID) {

        return adminUserService.deleteUserProcess(userID);

    }

    @AdminLoggable(
            pageType = AdminPageType.USER_MANAGEMENT,
            actionType = AdminActionType.CREATE,
            targetType = AdminTargetType.COMMUNITY_ACTION,
            targetId = "#reportID",
            description = "유저 커뮤니티 정지 조치(타겟 ID는 정지 근거 신고ID)"
    )
    @PatchMapping("/communityBan/{reportId}")
    public Map<String, String> banUserCommunity(@PathVariable Long reportId) {

        return adminUserService.banUserCommunityBanProcess(reportId, SanctionType.COMMUNITY_BAN);

    }

    @AdminLoggable(
            pageType = AdminPageType.USER_MANAGEMENT,
            actionType = AdminActionType.UPDATE,
            targetType = AdminTargetType.COMMUNITY_ACTION,
            targetId = "#userID",
            description = "유저 커뮤니티 정지 취소"
    )
    @PatchMapping("/reactive/community/{userId}")
    public Map<String, String> reactiveUserCommunity(@PathVariable String userId) {

        return adminUserService.reactiveCommunityProcess(userId);

    }

    @DeleteMapping("/test/delete/{userId}")
    public Map<String, String> deleteTestUser(@PathVariable String userId) {

        return adminUserService.testDeleteUser(userId);

    }
}
