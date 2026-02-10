package com.example.Petbulance_BE.domain.terms.service;

import com.example.Petbulance_BE.domain.terms.dto.res.TermsConsentsResDto;
import com.example.Petbulance_BE.domain.terms.dto.res.TermsStatusResDto;
import com.example.Petbulance_BE.domain.terms.enums.TermsStatus;
import com.example.Petbulance_BE.domain.terms.enums.TermsType;
import com.example.Petbulance_BE.domain.terms.dto.req.ConsentsReqDto;
import com.example.Petbulance_BE.domain.terms.dto.res.GetTermsResDto;
import com.example.Petbulance_BE.domain.terms.entity.Terms;
import com.example.Petbulance_BE.domain.terms.entity.UserAgreementHistory;
import com.example.Petbulance_BE.domain.terms.repository.TermsJpaRepository;
import com.example.Petbulance_BE.domain.terms.repository.UserAgreementHistoryRepository;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.domain.user.repository.UsersJpaRepository;
import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import com.example.Petbulance_BE.global.common.type.Role;
import com.example.Petbulance_BE.global.util.JWTUtil;
import com.example.Petbulance_BE.global.util.UserUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TermsService {

    private final UsersJpaRepository usersJpaRepository;
    private final TermsJpaRepository termsJpaRepository;
    private final UserAgreementHistoryRepository userAgreementHistory;
    private final UserUtil userUtil;
    private final JWTUtil jwtUtil;

    public List<GetTermsResDto> getTermsProcess() {

        List<Terms> allActive = termsJpaRepository.findAllActive();

        List<GetTermsResDto> list = allActive.stream()
                .sorted(Comparator.comparingInt(a -> a.getType().getSort()))
                .map(a -> GetTermsResDto.builder()
                .id(a.getId())
                .termsType(a.getType())
                .title(a.getType().getDescription())
                .required(a.getIsRequired())
                .summary(a.getContent())
                .version(a.getVersion())
                .build()
        ).toList();

        return list;

    }

    public GetTermsResDto getOneTermsProcess(TermsType type) {

        Terms oneActive = termsJpaRepository.findOneActive(type).orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND_TERM));

        return GetTermsResDto.builder()
                .id(oneActive.getId())
                .title(oneActive.getType().getDescription())
                .required(oneActive.getIsRequired())
                .summary(oneActive.getContent())
                .version(oneActive.getVersion())
                .build();

    }

    @Transactional
    public TermsConsentsResDto consentsProcess(ConsentsReqDto dto, HttpServletRequest request) {

        Users user = usersJpaRepository.findById(userUtil.getCurrentUser().getId()).orElseThrow(()-> new CustomException(ErrorCode.NON_EXIST_USER));

        // 리스트로 들어온 모든 ID를 한 번에 이력 객체로 변환
        List<UserAgreementHistory> histories = dto.getTermsId().stream()
                .map(id -> UserAgreementHistory.builder()
                        .user(user)
                        .terms(termsJpaRepository.getReferenceById(id))
                        .isAgreed(true)
                        .agreedAt(LocalDateTime.now())
                        .build())
                .toList();

        userAgreementHistory.saveAll(histories);

        Integer count = userAgreementHistory.countUserAgreement(user);

        if(count == null || count < 3){
            throw new CustomException(ErrorCode.REQUIRED_TERMS_MISSING);
        }

        if(user.getRole() == Role.ROLE_TEMPORAL){
            user.setRole(Role.ROLE_CLIENT);
        }

        String authorization = request.getHeader("Authorization");
        String token = authorization.split(" ")[1];

        String provider = jwtUtil.getProvider(token);

        String access = jwtUtil.createJwt(user.getId(), "access", String.valueOf(user.getRole()), provider);

        return new TermsConsentsResDto(access);

    }

    public TermsStatusResDto statusProcess() {

        Users users = userUtil.getCurrentUser();

        List<UserAgreementHistory> latestThumbprints = userAgreementHistory.findLatestThumbprints(users.getId());

        Map<TermsType, UserAgreementHistory> historyMap = latestThumbprints.stream()
                .collect(Collectors.toMap(
                        h -> h.getTerms().getType(), // Terms의 Type (SERVICE, PRIVACY 등)
                        h -> h
                ));

        TermsStatusResDto resDto = new TermsStatusResDto();

        resDto.setService(calculateStatus(historyMap.get(TermsType.SERVICE)));
        resDto.setPrivacy(calculateStatus(historyMap.get(TermsType.PRIVACY)));
        resDto.setLocation(calculateStatus(historyMap.get(TermsType.LOCATION)));
        resDto.setMarketing(calculateStatus(historyMap.get(TermsType.MARKETING)));

        return resDto;
    }


    private TermsStatus calculateStatus(UserAgreementHistory history) {

        if (history == null) {
            return TermsStatus.DISAGREE;
        }

        return history.getTerms().getIsActive() ? TermsStatus.AGREE : TermsStatus.EXPIRED;
    }

    @Transactional
    public Map<String, String> deleteOneTermsProcess(TermsType type) {

        Users currentUser = userUtil.getCurrentUser();

        Integer i = userAgreementHistory.disAgreeTerms(type, currentUser);

        if(i.equals(0)){
            throw new CustomException(ErrorCode.NOT_FOUND_AGREE);
        }

        return Map.of("message", "약관이 철회되었습니다.");

    }
}
