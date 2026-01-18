package com.example.Petbulance_BE.domain.review.aop;

import com.example.Petbulance_BE.domain.admin.user.service.AdminUserService;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.global.util.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class ReviewAccessAspect {

    private final AdminUserService adminUserService;

    @Before("@annotation(com.example.Petbulance_BE.domain.review.aop.CheckReviewAvailable)")
    public void reviewBannedCheck() {
        Users currentUser = UserUtil.getCurrentUser();

        if(currentUser != null) {
            adminUserService.checkReviewAccess(currentUser);
        }
    }
}
