package com.example.Petbulance_BE.domain.admin.user.controller;

import com.example.Petbulance_BE.domain.admin.page.PageResponse;
import com.example.Petbulance_BE.domain.admin.user.dto.GetUserQueryParam;
import com.example.Petbulance_BE.domain.admin.user.dto.GetUsersResDto;
import com.example.Petbulance_BE.domain.admin.user.service.AdminUserService;
import com.example.Petbulance_BE.domain.report.service.CommunitySanctionService;
import com.example.Petbulance_BE.domain.user.type.SanctionType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;
    private final CommunitySanctionService communitySanctionService;

    @GetMapping("/search")
    public PageResponse<GetUsersResDto> getUsers(@PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
                                                 @ModelAttribute GetUserQueryParam queryParam) {
        return adminUserService.getUsersProcess(pageable, queryParam);

    }

    @PatchMapping("/reviewBan/{reportId}")
    public Map<String, String> reviewBan(@PathVariable Long reportId) {

        adminUserService.banUserReviewProcess(reportId, SanctionType.REVIEW_BAN);

        return Map.of("message", "success");

    }

    @PatchMapping("/reactive/review/{userId}")
    public Map<String,String> reactiveReview(@PathVariable String userId) {

        adminUserService.reactiveReviewProcess(userId);

        return Map.of("message", "success");

    }

    @DeleteMapping("/delete/{userID}")
    public String deleteUser(@PathVariable String userID) {

        return adminUserService.deleteUserProcess(userID);

    }

    @PatchMapping("/communityBan/{reportId}")
    public Map<String, String> banUserCommunity(@PathVariable Long reportId) {

        return adminUserService.banUserCommunityBanProcess(reportId, SanctionType.COMMUNITY_BAN);

    }

    @PatchMapping("/reactive/community/{userId}")
    public Map<String, String> reactiveUserCommunity(@PathVariable String userId) {

        return adminUserService.reactiveCommunityProcess(userId);

    }

}
