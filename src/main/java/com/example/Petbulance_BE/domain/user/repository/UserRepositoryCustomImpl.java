package com.example.Petbulance_BE.domain.user.repository;

import com.example.Petbulance_BE.domain.admin.user.dto.GetUserQueryParam;
import com.example.Petbulance_BE.domain.admin.user.dto.GetUsersResDto;
import com.example.Petbulance_BE.global.common.type.Role;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import com.example.Petbulance_BE.domain.user.entity.QUsers;

import static com.example.Petbulance_BE.domain.user.entity.QUserSanction.userSanction;
import static com.example.Petbulance_BE.domain.user.entity.QUsers.users;
import static com.example.Petbulance_BE.domain.userEmail.entity.QUserEmails.userEmails;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom{

    private final JPAQueryFactory query;

    @Override
    public PageImpl<GetUsersResDto> adminGetUsers(Pageable pageable, GetUserQueryParam getUserQueryParam) {

        List<GetUsersResDto> content = query.select(Projections.fields(GetUsersResDto.class,
                                users.id.as("userId"),
                                users.nickname.as("nickname"),
                                new CaseBuilder()
                                        .when(users.firstLogin.eq("KAKAO")).then(userEmails.kakaoEmail)
                                        .when(users.firstLogin.eq("NAVER")).then(userEmails.naverEmail)
                                        .when(users.firstLogin.eq("GOOGLE")).then(userEmails.googleEmail)
                                        .otherwise("")
                                        .as("email"),
                                users.firstLogin.as("signUpPath"),
                                getWarningCount().as("warnings"),
                                reviewStatus().as("reviewBan"),
                                communityStatus().as("communityBan"),
                                users.createdAt.as("createdAt")
                        )
                )
                .from(users)
                .leftJoin(users.userEmails, userEmails)
                .where(
                        getUserNicknameAndEmail(getUserQueryParam),
                        getUserSignUpPath(getUserQueryParam),
                        getUserStatus(getUserQueryParam),
                        adminOnly(getUserQueryParam)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = query
                .select(users.countDistinct())
                .from(users)
                .leftJoin(users.userEmails, userEmails)
                .where(
                        getUserNicknameAndEmail(getUserQueryParam),
                        getUserSignUpPath(getUserQueryParam),
                        getUserStatus(getUserQueryParam),
                        adminOnly(getUserQueryParam)
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);


    }

    private BooleanExpression getUserNicknameAndEmail(GetUserQueryParam getUserQueryParam) {
        String usernameOrEmail = getUserQueryParam.getUsernameOrEmail();

        if (usernameOrEmail == null || usernameOrEmail.isEmpty()) {
            return null;
        }

        return users.nickname.eq(usernameOrEmail)
                .or(userEmails.kakaoEmail.eq(usernameOrEmail))
                .or(userEmails.naverEmail.eq(usernameOrEmail))
                .or(userEmails.googleEmail.eq(usernameOrEmail));
    }

    private BooleanExpression getUserSignUpPath(GetUserQueryParam getUserQueryParam) {
        String signUpPath = getUserQueryParam.getSignUpPath();
        if (signUpPath == null || signUpPath.isEmpty()) {
            return null;
        }

        return users.firstLogin.eq(signUpPath);
    }

    private BooleanExpression adminOnly(GetUserQueryParam getUserQueryParam) {
        Role userType = getUserQueryParam.getUserType();
        if(userType == null) {
            return null;
        }
        return users.role.eq(userType);
    }

    private BooleanExpression getUserStatus(GetUserQueryParam getUserQueryParam) {
        String userStatus = getUserQueryParam.getUserStatus();
        if (userStatus == null || userStatus.isEmpty()) {
            return null;
        }
        if(userStatus.equals("REVIEWBAN")){
            return users.reviewBanUntil.after(LocalDateTime.now());
        }
        if(userStatus.equals("COMMUNITYBAN")){
            return users.communityBanUntil.after(LocalDateTime.now());
        }
        if(userStatus.equals("BOTH")){
            return users.reviewBanUntil.after(LocalDateTime.now()).and(users.communityBanUntil.after(LocalDateTime.now()));
        }
        return null;
    }

    private NumberExpression<Integer> getWarningCount() {
        return Expressions.asNumber(JPAExpressions.select(userSanction.count()).from(userSanction).where(userSanction.user.eq(users))).intValue().coalesce(0);
    }

    private BooleanExpression reviewStatus() {
        return users.reviewBanUntil.isNotNull()
                .and(users.reviewBanUntil.after(LocalDateTime.now()));
    }

    private BooleanExpression communityStatus() {
        return users.communityBanUntil.isNotNull()
                .and(users.communityBanUntil.after(LocalDateTime.now()));
    }

}
