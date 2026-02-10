package com.example.Petbulance_BE.domain.report.aop.communityBan;


import com.example.Petbulance_BE.domain.report.service.CommunitySanctionService;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.global.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class CommunityAccessAspect {

    private final CommunitySanctionService communitySanctionService;

    @Before("@annotation(com.example.Petbulance_BE.domain.report.aop.communityBan.CheckCommunityAvailable)")
    public void check() {
        Users currentUser = UserUtil.getCurrentUser();
        assert currentUser != null;
        communitySanctionService.checkCommunityAccess(currentUser);
    }
}
