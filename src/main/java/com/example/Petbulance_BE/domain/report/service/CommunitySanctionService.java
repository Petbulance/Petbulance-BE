package com.example.Petbulance_BE.domain.report.service;

import com.example.Petbulance_BE.domain.report.entity.Report;
import com.example.Petbulance_BE.domain.report.exception.CommunityBannedException;
import com.example.Petbulance_BE.domain.report.type.ReportActionType;
import com.example.Petbulance_BE.domain.user.entity.UserSanction;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.domain.user.repository.UserSanctionRepository;
import com.example.Petbulance_BE.domain.user.repository.UsersJpaRepository;
import com.example.Petbulance_BE.domain.user.type.SactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class CommunitySanctionService {

    private static final long DEFAULT_SUSPEND_DAYS = 7L; // 기본 7일 정지

    private final UserSanctionRepository userSanctionRepository;
    private final UsersJpaRepository userRepository;

    /**
     * 관리자 페이지에서 report actionType 을 SUSPEND 로 변경했을 때 호출
     */
    public void applySanctionForReport(Report report, SactionType sactionType) {
        if (report.getActionType() != ReportActionType.SUSPEND) {
            // SUSPEND 가 아니면 제재 안 함
            return;
        }

        // 신고 대상자
        Users targetUser = report.getTargetUser();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime until = now.plusDays(DEFAULT_SUSPEND_DAYS);

        // 유저 상태 업데이트
        targetUser.banCommunityUntil(until);

        // 제재 이력 저장
        UserSanction sanction = UserSanction.builder()
                .user(targetUser)
                .sanctionType(sactionType)
                .reason("[신고ID=" + report.getReportId() + "] " + report.getReportReason())
                .startAt(now)
                .endAt(until)
                .active(true)
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
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));

        // 커뮤니티 정지 해제
        user.clearCommunityBan();

        // 활성화된 커뮤니티 제재 이력 비활성화
        userSanctionRepository
                .findAllByUserAndActiveTrue(user)
                .forEach(sanction -> sanction.deactivate());
    }
}
