package com.example.Petbulance_BE.domain.report.service;

import com.example.Petbulance_BE.domain.device.entity.Device;
import com.example.Petbulance_BE.domain.device.repository.DeviceJpaRepository;
import com.example.Petbulance_BE.domain.notification.service.NotificationService;
import com.example.Petbulance_BE.domain.notification.type.NotificationTargetType;
import com.example.Petbulance_BE.domain.notification.type.NotificationType;
import com.example.Petbulance_BE.domain.report.entity.Report;
import com.example.Petbulance_BE.domain.report.exception.CommunityBannedException;
import com.example.Petbulance_BE.domain.user.entity.UserSanction;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.domain.user.repository.UserSanctionRepository;
import com.example.Petbulance_BE.domain.user.repository.UsersJpaRepository;
import com.example.Petbulance_BE.domain.user.type.SanctionType;
import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import com.example.Petbulance_BE.global.firebase.FcmService;
import com.example.Petbulance_BE.global.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class CommunitySanctionService {

    private static final long DEFAULT_SUSPEND_DAYS = 7L; // 기본 7일 정지

    private final UserSanctionRepository userSanctionRepository;
    private final UsersJpaRepository userRepository;
    private final FcmService fcmService;
    private final DeviceJpaRepository deviceJpaRepository;
    private final NotificationService notificationService;

    /**
     * 관리자 페이지에서 report actionType 을 SUSPEND 로 변경했을 때 호출
     */
    public void applySanctionForReport(Report report, SanctionType sanctionType) {

        Users currentUser = UserUtil.getCurrentUser();

        // 신고 대상자
        Users targetUser = report.getTargetUser();

        // 신고 대상자가 '커뮤니티 7일 정지' 리겨 횟수 조회 (활성화된 것)
        long sanctionCount = userSanctionRepository.countByUserAndSanctionTypeAndActiveTrue(targetUser, SanctionType.COMMUNITY_BAN);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime until = now.plusDays(DEFAULT_SUSPEND_DAYS);
        String reasonPrefix;

        // 영구 정지 로직 추가: 기존 이력이 2개 이상이면 3회차에서 영구 정지
        if (sanctionCount >= 2) {
            until = now.plusYears(100); // 사실상 영구 정지
            reasonPrefix = "[영구 정지/누적 3회차] ";

            sendPermanentBanAlarm(targetUser, currentUser);
        } else {
            until = now.plusDays(DEFAULT_SUSPEND_DAYS); // 기존 7일 정지
            reasonPrefix = String.format("[%d회차 정지] ", sanctionCount + 1);

            send7DaysBanAlarm(targetUser, currentUser);
        }

        // 유저 상태 업데이트
        if (sanctionType == SanctionType.COMMUNITY_BAN) {
            targetUser.banCommunityUntil(until);
        } else if (sanctionType == SanctionType.REVIEW_BAN) {
            targetUser.banReviewUntil(until);
        }

        // 제재 이력 저장
        UserSanction sanction = UserSanction.builder()
                .user(targetUser)
                .sanctionType(sanctionType)
                .reason("[신고ID=" + report.getReportId() + "] " + report.getReportReason())
                .startAt(now)
                .endAt(until)
                .active(true)
                .adminId(currentUser.getId())
                .build();

        userSanctionRepository.save(sanction);
    }

    /**
     * 커뮤니티 기능 사용 가능 여부 체크
     */
    public void checkCommunityAccess(Users user) {
        LocalDateTime banUntil = user.getCommunityBanUntil();

        if (banUntil == null) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        if (banUntil.isAfter(now)) {
            // 아직 정지 기간
            throw new CommunityBannedException(banUntil);
        } else {
            // 정지 기간 지났으면 자동 해제
            user.clearCommunityBan();
        }
    }

    public void unbanCommunity(String userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NON_EXIST_USER));

        // 커뮤니티 정지 해제
        user.clearCommunityBan();

        // 활성화된 커뮤니티 제재 이력 비활성화
        userSanctionRepository
                .findAllByUserAndActiveTrueAndSanctionType(user, SanctionType.COMMUNITY_BAN)
                .forEach(sanction -> sanction.deactivate());
    }

    private void send7DaysBanAlarm(Users targetUser, Users currentUser) {
        Device device = deviceJpaRepository.findByUserId(targetUser.getId());

        String title = "커뮤니티 이용 제한 안내";
        String message = "경고 누적으로 인해 커뮤니티 이용이 7일간 정지되었습니다.";

        if (device != null && device.getFcm_token() != null) {
            Map<String, String> data = new HashMap<>();
            data.put("type", "BAN_7_DAYS");
            fcmService.sendPushNotification(device.getFcm_token(), title, message, data);
        }

        notificationService.createNotification(targetUser, currentUser, NotificationType.TEMP_BAN_7D, NotificationTargetType.SANCTION, null, message);
    }

    private void sendPermanentBanAlarm(Users targetUser, Users currentUser) {
        Device device = deviceJpaRepository.findByUserId(targetUser.getId());

        String title = "커뮤니티 이용 제한 안내";
        String message = "경고 누적으로 인해 커뮤니티 이용이 영구적으로 정지되었습니다.";

        if (device != null && device.getFcm_token() != null) {
            Map<String, String> data = new HashMap<>();
            data.put("type", "BAN_PERMANENT");
            fcmService.sendPushNotification(device.getFcm_token(), title, message, data);
        }

        notificationService.createNotification(targetUser, currentUser, NotificationType.PERMANENT_BAN, NotificationTargetType.SANCTION, null, message);
    }
}
