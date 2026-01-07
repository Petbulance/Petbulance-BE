package com.example.Petbulance_BE.domain.admin.user.service;

import com.example.Petbulance_BE.domain.admin.page.PageResponse;
import com.example.Petbulance_BE.domain.admin.user.dto.GetUserQueryParam;
import com.example.Petbulance_BE.domain.admin.user.dto.GetUsersResDto;
import com.example.Petbulance_BE.domain.admin.user.dto.ReactiveReviewReq;
import com.example.Petbulance_BE.domain.admin.user.dto.ReviewBanReqDto;
import com.example.Petbulance_BE.domain.report.entity.Report;
import com.example.Petbulance_BE.domain.report.service.CommunitySanctionService;
import com.example.Petbulance_BE.domain.user.entity.UserSanction;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.domain.user.repository.UserSanctionRepository;
import com.example.Petbulance_BE.domain.user.repository.UsersJpaRepository;
import com.example.Petbulance_BE.domain.user.type.SactionType;
import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UsersJpaRepository usersJpaRepository;
    private final UserSanctionRepository userSanctionRepository;
    private final CommunitySanctionService communitySanctionService;

    public PageResponse<GetUsersResDto> getUsersProcess(Pageable pageable, GetUserQueryParam queryParam) {

        PageImpl<GetUsersResDto> getUsersResDtos = usersJpaRepository.adminGetUsers(pageable, queryParam);

        return new PageResponse<>(getUsersResDtos);

    }

    @Transactional
    public Long banUserReviewProcess(ReviewBanReqDto reviewBanReqDto) {

        Users user = usersJpaRepository.findById(reviewBanReqDto.getId()).orElseThrow(() -> new CustomException(ErrorCode.NON_EXIST_USER));

        int day = reviewBanReqDto.getDay();

        String reason = reviewBanReqDto.getReason();

        user.setCommunityBanUntil(LocalDateTime.now().plusDays(day));

        UserSanction userSanction = UserSanction.builder()
                .user(user)
                .sanctionType(SactionType.REVIEW_BAN)
                .reason(reason)
                .startAt(LocalDateTime.now())
                .endAt(LocalDateTime.now().plusDays(day))
                .active(true)
                .build();

        userSanctionRepository.save(userSanction);

        return userSanction.getId();
    }

    @Transactional
    public String reactiveReviewProcess(ReactiveReviewReq reactiveReviewReq) {

        String userId = reactiveReviewReq.getUserID();

        Users user = usersJpaRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.NON_EXIST_USER));

        user.setReviewBanUntil(null);

        List<UserSanction> byUserAndActiveIsTrue = userSanctionRepository.findByUserAndActiveIsTrue(user);

        byUserAndActiveIsTrue.forEach(userSanction -> userSanction.setActive(false));

        return userId;
    }

    @Transactional
    public String deleteUserProcess(String userID) {

        Users users = usersJpaRepository.findById(userID).orElseThrow(() -> new CustomException(ErrorCode.NON_EXIST_USER));

        users.setDeleted(true);

        return userID;
    }

    @Transactional
    public Map<String, String> banUserCommnityBanProcess(Report report, SactionType sactionType) {

        communitySanctionService.applySanctionForReport(report, sactionType);

        return Map.of("message", "success");

    }


    @Transactional
    public Map<String, String> reactiveCommunityProcess(Report report, SactionType sactionType) {

        String targetUser = report.getTargetUser().getId();

        Users byId = usersJpaRepository.findById(targetUser).orElseThrow(()->new CustomException(ErrorCode.NON_EXIST_USER));

        byId.setCommunityBanUntil(null);

        return Map.of("message", "success");

    }
}
