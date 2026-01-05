package com.example.Petbulance_BE.domain.admin.user.controller;

import com.example.Petbulance_BE.domain.admin.page.PageResponse;
import com.example.Petbulance_BE.domain.admin.user.dto.GetUserQueryParam;
import com.example.Petbulance_BE.domain.admin.user.dto.GetUsersResDto;
import com.example.Petbulance_BE.domain.admin.user.dto.ReactiveReviewReq;
import com.example.Petbulance_BE.domain.admin.user.dto.ReviewBanReqDto;
import com.example.Petbulance_BE.domain.admin.user.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping("/search")
    public PageResponse<GetUsersResDto> getUsers(@PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
                                                 @ModelAttribute GetUserQueryParam queryParam) {
        return adminUserService.getUsersProcess(pageable, queryParam);

    }

    @PostMapping("/reviewBan")
    public Long reviewBan(@RequestBody ReviewBanReqDto reviewBanReqDto) {

        return adminUserService.banUserReviewProcess(reviewBanReqDto);

    }

    @PatchMapping("/reactive/review")
    public String reactiveReview(@RequestBody ReactiveReviewReq reactiveReviewReq) {

        return adminUserService.reactiveReviewProcess(reactiveReviewReq);

    }

    @DeleteMapping("/delete/{userID}")
    public String deleteUser(@PathVariable String userID) {

        return adminUserService.deleteUserProcess(userID);

    }



}
