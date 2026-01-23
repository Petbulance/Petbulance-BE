package com.example.Petbulance_BE.domain.admin.user.service;

import com.example.Petbulance_BE.domain.admin.page.PageResponse;
import com.example.Petbulance_BE.domain.admin.user.dto.GetUserQueryParam;
import com.example.Petbulance_BE.domain.admin.user.dto.GetUsersResDto;
import com.example.Petbulance_BE.domain.admin.user.dto.OneUserResDto;
import com.example.Petbulance_BE.domain.admin.user.exception.ReviewBannedException;
import com.example.Petbulance_BE.domain.report.entity.Report;
import com.example.Petbulance_BE.domain.report.repository.ReportRepository;
import com.example.Petbulance_BE.domain.report.service.CommunitySanctionService;
import com.example.Petbulance_BE.domain.report.type.ReportActionType;
import com.example.Petbulance_BE.domain.user.entity.UserSanction;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.domain.user.repository.UserSanctionRepository;
import com.example.Petbulance_BE.domain.user.repository.UsersJpaRepository;
import com.example.Petbulance_BE.domain.user.type.SanctionType;
import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import com.example.Petbulance_BE.global.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private static final long DEFAULT_SUSPEND_DAYS = 7;
    private final UsersJpaRepository usersJpaRepository;
    private final UserSanctionRepository userSanctionRepository;
    private final CommunitySanctionService communitySanctionService;
    private final ReportRepository reportRepository;
    private final UserUtil userUtil;

    public PageResponse<GetUsersResDto> getUsersProcess(Pageable pageable, GetUserQueryParam queryParam) {

        PageImpl<GetUsersResDto> getUsersResDtos = usersJpaRepository.adminGetUsers(pageable, queryParam);

        return new PageResponse<>(getUsersResDtos);

    }

    @Transactional
    public void banUserReviewProcess(Long reportId, SanctionType sanctionType) {

        Users currentUser = userUtil.getCurrentUser();

        Report report = reportRepository.findById(reportId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REPORT));

        if(report.getActionType() != ReportActionType.SUSPEND){
            return;
        }

        Users targetUser = report.getTargetUser();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime until = now.plusDays(DEFAULT_SUSPEND_DAYS);

        targetUser.banReviewUntil(until);

        UserSanction userSanction = UserSanction.builder()
                .user(targetUser)
                .sanctionType(sanctionType)
                .reason("[신고ID=" + report.getReportId() + "] " + report.getReportReason())
                .startAt(now)
                .endAt(until)
                .active(true)
                .adminId(currentUser.getId())
                .build();

        userSanctionRepository.save(userSanction);
    }

    @Transactional
    public void reactiveReviewProcess(String userId) {

        Users users = usersJpaRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.NON_EXIST_USER));

        users.clearReviewBan();

        List<UserSanction> sactions = userSanctionRepository.findAllByUserAndActiveTrueAndSanctionType(users, SanctionType.REVIEW_BAN);

        sactions.forEach(saction -> saction.deactivate());

    }

    @Transactional
    public String deleteUserProcess(String userID) {

        Users users = usersJpaRepository.findById(userID).orElseThrow(() -> new CustomException(ErrorCode.NON_EXIST_USER));

        users.setDeleted(true);

        return userID;
    }

    @Transactional
    public Map<String, String> banUserCommunityBanProcess(Long id, SanctionType sanctionType) {

        Report report = reportRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REPORT));

        communitySanctionService.applySanctionForReport(report, sanctionType);

        return Map.of("message", "success");

    }


    @Transactional
    public Map<String, String> reactiveCommunityProcess(String userId) {

        communitySanctionService.unbanCommunity(userId);

        return Map.of("message", "success");

    }

    @Transactional
    public void checkReviewAccess(Users user){
        LocalDateTime banUntil = user.getReviewBanUntil();

        if(banUntil == null){
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        if(banUntil.isAfter(now)){
            throw new ReviewBannedException(banUntil);
        }else{
            user.clearReviewBan();
        }
    }

    public OneUserResDto getDetailUser(String userID) {

        OneUserResDto oneUserResDto = new OneUserResDto();

        Users users = usersJpaRepository.findById(userID).orElseThrow(() -> new CustomException(ErrorCode.NON_EXIST_USER));
        List<UserSanction> byUserId = userSanctionRepository.findByUserId(userID);

        String userStatus = "정상계정";

        if(users.getSuspended()!= null && users.getSuspended()){
            userStatus = "정지계정";
        }else if(users.getDeleted()!= null && users.getDeleted()){
            userStatus = "삭제계정";
        }


        List<OneUserResDto.ReportInfo> list = byUserId.stream().map(m -> OneUserResDto.ReportInfo.builder()
                .reportTime(m.getCreatedAt())
                .actionContent(m.getSanctionType())
                .actionReason(m.getReason())
                .adminId(m.getAdminId())
                .build()).toList();

        oneUserResDto.setSignUpPath(users.getFirstLogin());
        oneUserResDto.setUserStatus(userStatus);
        oneUserResDto.setSignUpTime(users.getCreatedAt());
        oneUserResDto.setWarningCount(byUserId.size());
        oneUserResDto.setReportInfo(list);

        return oneUserResDto;
    }
}
