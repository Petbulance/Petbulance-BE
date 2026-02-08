package com.example.Petbulance_BE.domain.user.service;

import com.example.Petbulance_BE.domain.user.dto.request.CheckProfileImageRequestDto;
import com.example.Petbulance_BE.domain.user.dto.request.NotificationSettingRequestDto;
import com.example.Petbulance_BE.domain.user.dto.request.ProfileImageUpdateRequestDto;
import com.example.Petbulance_BE.domain.user.dto.request.SocialConnectRequestDto;
import com.example.Petbulance_BE.domain.user.dto.response.*;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.domain.user.repository.UsersJpaRepository;
import com.example.Petbulance_BE.domain.userEmail.entity.UserEmails;
import com.example.Petbulance_BE.domain.userEmail.repository.UserEmailsJpaRepository;
import com.example.Petbulance_BE.domain.userSetting.entity.UserAuthority;
import com.example.Petbulance_BE.domain.userSetting.entity.UserSetting;
import com.example.Petbulance_BE.domain.userSetting.repository.UserSettingJpaRepository;
import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import com.example.Petbulance_BE.global.common.s3.S3Service;
import com.example.Petbulance_BE.global.util.JWTUtil;
import com.example.Petbulance_BE.global.util.UserUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UsersJpaRepository usersJpaRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final UserEmailsJpaRepository userEmailsJpaRepository;
    private final S3Service s3Service;
    private final JWTUtil jwtUtil;
    private final UserSettingJpaRepository userSettingJpa;
    private final UserSettingJpaRepository userSettingJpaRepository;
    private final UserUtil userUtil;

    public NicknameResponseDto checkNicknameProcess(String nickname) {
        Boolean exists = usersJpaRepository.existsByNickname(nickname);
        String reason = null;
        if(exists) {
            reason = "이미 등록된 닉네임입니다.";
        }
        return NicknameResponseDto.builder()
                .nickname(nickname)
                .available(!exists)
                .reason(reason).build();
    }

    @Transactional
    public NicknameSaveResponseDto saveNicknameProcess(String nickname) {
        Users currentUser = usersJpaRepository.findById(UserUtil.getCurrentUser().getId())
                        .orElseThrow(()-> new CustomException(ErrorCode.NON_EXIST_USER));
        currentUser.setNickname(nickname);
        return NicknameSaveResponseDto.builder()
                .message("닉네임 저장이 완료되었습니다.")
                .build();
    }

    @Transactional
    public NicknameUpdateResponseDto updateNicknameProcess(String requestNickname) {
        Users currentUser = usersJpaRepository.findById(UserUtil.getCurrentUser().getId())
                .orElseThrow(()-> new CustomException(ErrorCode.NON_EXIST_USER));
        currentUser.setNickname(requestNickname);
        return NicknameUpdateResponseDto.builder()
                .message("닉네임 수정이 완료되었습니다.")
                .build();
    }

    @Transactional
    public SocialConnectResponseDto socialConnectProcess(SocialConnectRequestDto socialConnectRequestDto) {

        String provider = socialConnectRequestDto.getProvider();

        String email;

        switch (provider) {
            case "KAKAO" -> email = getKakaoEmail(socialConnectRequestDto.getAuthCode());
            case "NAVER" -> email = getNaverEmail(socialConnectRequestDto.getAuthCode());
            case "GOOGLE" -> email = getGoogleEmail(socialConnectRequestDto.getAuthCode());
            default -> throw new CustomException(ErrorCode.INVALID_PROVIDER);
        }

        Users currentUser = UserUtil.getCurrentUser();

        Users user = usersJpaRepository.findById(UserUtil.getCurrentUser().getId()).get();
        UserEmails userEmails = userEmailsJpaRepository.findById(UserUtil.getCurrentUser().getId()).get();

        Optional<Users> optionalUser = userEmailsJpaRepository.findByEmailByProvider(provider, email);

        if(optionalUser.isPresent()) {
            throw new CustomException(ErrorCode.SNS_ACCOUNT_ALREADY_LINKED);
        }

        switch (provider) {
            case "KAKAO" -> {
                user.setKakaoConnected(true);
                userEmails.setKakaoEmail(email);
            }
            case "NAVER" -> {
                user.setNaverConnected(true);
                userEmails.setNaverEmail(email);
            }
            case "GOOGLE" -> {
                user.setGoogleConnected(true);
                userEmails.setGoogleEmail(email);
            }
        }

        return new SocialConnectResponseDto("계정이 성공적으로 연결되었습니다.");

    }

    private String getKakaoEmail(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        ResponseEntity<String> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                new HttpEntity<>(headers),
                String.class
        );

        try {
            JsonNode node = new ObjectMapper().readTree(response.getBody());
            return node.path("kakao_account").path("email").asText();
        } catch (Exception e) {
            throw new CustomException(ErrorCode.FAIL_KAKAO_LOGIN);
        }
    }

    private String getNaverEmail(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );

        try {
            JsonNode node = new ObjectMapper().readTree(response.getBody());
            return node.path("response").path("email").asText();
        } catch (Exception e) {
            throw new CustomException(ErrorCode.FAIL_NAVER_LOGIN);
        }
    }

    private String getGoogleEmail(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://www.googleapis.com/oauth2/v2/userinfo",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );

        try {
            JsonNode node = new ObjectMapper().readTree(response.getBody());
            return node.path("email").asText();
        } catch (Exception e) {
            throw new CustomException(ErrorCode.FAIL_GOOGLE_LOGIN);
        }
    }

    @Transactional
    public Map<String,String> disconnectAccountProcess(String platform){
        Users currentUser = UserUtil.getCurrentUser();

        // getCurrentUser()는 시큐리티 컨텍스트에서 꺼내온 detached 엔티티일 수 있으므로
        // 영속성 컨텍스트에 등록된 엔티티를 가져옴
        Users user = usersJpaRepository.findById(currentUser.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.NON_EXIST_USER));

        UserEmails userEmails = userEmailsJpaRepository.findById(UserUtil.getCurrentUser().getId()).get();

        int connectedCount = 0;

        if (Boolean.TRUE.equals(user.getKakaoConnected())) connectedCount++;
        if (Boolean.TRUE.equals(user.getNaverConnected())) connectedCount++;
        if (Boolean.TRUE.equals(user.getGoogleConnected())) connectedCount++;

        if(connectedCount == 1){
            throw new CustomException(ErrorCode.CANNOT_DISCONNECT_LAST_LOGIN_METHOD);
        }

        switch (platform) {
            case "KAKAO" -> {
                user.setKakaoConnected(false);
                userEmails.setKakaoEmail(null);
            }
            case "NAVER" -> {
                user.setNaverConnected(false);
                userEmails.setNaverEmail(null);
            }
            case "GOOGLE" -> {
                user.setGoogleConnected(false);
                userEmails.setGoogleEmail(null);
            }
        }
        return Map.of("message",String.format("%s 계정 연결이 성공적으로 해제되었습니다.", platform));
    }

    public ProfileImageUpdateResponseDto updateProfileImageProcess(ProfileImageUpdateRequestDto profileImageUpdateRequestDto) {
        String contentType = profileImageUpdateRequestDto.getContentType();
        String filename = profileImageUpdateRequestDto.getFilename();

        Users currentUser = UserUtil.getCurrentUser();
        String userId = Objects.requireNonNull(currentUser).getId();

        String uuid = UUID.randomUUID().toString();

        String key = "profileImage/" + uuid + "/" + filename;
        URL presignedPutUrl = s3Service.createPresignedPutUrl(key, contentType, 300);
        String imageUrl = s3Service.getObject(key);

        ProfileImageUpdateResponseDto profileImageUpdateResponseDto = new ProfileImageUpdateResponseDto();
        profileImageUpdateResponseDto.setPreSignedUrl(presignedPutUrl.toString());
        profileImageUpdateResponseDto.setImageUrl(imageUrl);
        profileImageUpdateResponseDto.setSaveId(uuid);

        return profileImageUpdateResponseDto;
    }

    @Transactional
    public Map<String, String> profileImageCheckProcess(CheckProfileImageRequestDto checkProfileImageRequestDto) {

        String imageUUID = checkProfileImageRequestDto.getSaveId();
        String filename = checkProfileImageRequestDto.getFilename();


        String key = "profileImage/" + imageUUID + "/" + filename;
        boolean isExist = s3Service.doesObjectExist(key);

        if(!isExist){
            throw new CustomException(ErrorCode.FAIL_IMAGE_UPLOAD);
        }else{

            Users currentUser = UserUtil.getCurrentUser();
            String userId = Objects.requireNonNull(currentUser).getId();

            Users user = usersJpaRepository.findById(userId).get();

            if(user.getProfileImage()==null){
                user.setProfileImage(imageUUID);
            }else{
                String profileImage = user.getProfileImage();
                if(!profileImage.equals("default_image/159833.png")){
                    s3Service.deleteObject(profileImage);
                }
                user.setProfileImage("profileImage/" + imageUUID + "/" + filename);
            }

            return Map.of("message", "이미지 저장에 성공하였습니다.");
        }
    }

    public MeResponseDto myInfoProcess(HttpServletRequest request) {

        String authorization = request.getHeader("Authorization");

        String token = authorization.split(" ")[1];

        String provider = jwtUtil.getProvider(token).toLowerCase();

        Users currentUser = UserUtil.getCurrentUser();
        Users user = usersJpaRepository.findByIdWithUserEmail(currentUser.getId()).orElseThrow(() -> new CustomException(ErrorCode.NON_EXIST_USER));
        List<UserEmails> userEmails1 = user.getUserEmails();

        UserEmails userEmails = userEmails1.get(0);


        String email;

        String url = s3Service.getObject(user.getProfileImage());

        switch (provider) {
            case "kakao" -> {
                email = userEmails.getKakaoEmail();
            }
            case "naver" -> {
                email = userEmails.getNaverEmail();
            }
            case "google" -> {
                email = userEmails.getGoogleEmail();
            }
            default -> {
                throw new CustomException(ErrorCode.INVALID_PROVIDER);
            }
        }

        return MeResponseDto.builder()
                .provider(provider)
                .email(email)
                .nickname(user.getNickname())
                .profileImageUrl(url)
                .kakaoEmail(userEmails.getKakaoEmail())
                .googleEmail(userEmails.getGoogleEmail())
                .naverEmail(userEmails.getNaverEmail())
                .build();

    }

    @Transactional
    public NotificationSettingResponseDto settingNotificationProcess(NotificationSettingRequestDto notificationSettingRequestDto) {

        Users currentUser = UserUtil.getCurrentUser();

        Users user = usersJpaRepository.findByIdWithUserSetting(currentUser.getId()).orElseThrow(()-> new CustomException(ErrorCode.NON_EXIST_USER));

        List<UserSetting> userSettings = user.getUserSetting();

        UserSetting userSetting;
        if (userSettings.isEmpty()) {
            userSetting = UserSetting.builder()
                    .totalPush(false)
                    .eventPush(false)
                    .marketingPush(false)
                    .user(user)
                    .build();

            userSettingJpaRepository.save(userSetting);
        } else {
            userSetting = userSettings.get(0);
        }

        Boolean notificationsEnabled = notificationSettingRequestDto.getNotificationsEnabled();

        userSetting.setTotalPush(notificationSettingRequestDto.getNotificationsEnabled());
        userSetting.setEventPush(notificationSettingRequestDto.getEventNotificationsEnabled());
        userSetting.setMarketingPush(notificationSettingRequestDto.getMarketingNotificationsEnabled());

        NotificationSettingResponseDto notificationSettingResponseDto = new NotificationSettingResponseDto();
        notificationSettingResponseDto.setNotificationsEnabled(notificationsEnabled);
        notificationSettingResponseDto.setEventNotificationsEnabled(notificationSettingRequestDto.getEventNotificationsEnabled());
        notificationSettingResponseDto.setMarketingNotificationsEnabled(notificationSettingRequestDto.getMarketingNotificationsEnabled());

        return notificationSettingResponseDto;

    }

    public NotificationSettingResponseDto getSettingNotification() {

        Users currentUser = userUtil.getCurrentUser();

        UserSetting firstByUser = userSettingJpaRepository.findFirstByUser(currentUser);

        return new NotificationSettingResponseDto(firstByUser.getTotalPush(), firstByUser.getEventPush(), firstByUser.getMarketingPush());


    }

    @Transactional
    public Map<String, String> userCloseAccountProcess() {

        Users currentUser = userUtil.getCurrentUser();

        Users users = usersJpaRepository.findById(currentUser.getId()).orElseThrow(() -> new CustomException(ErrorCode.NON_EXIST_USER));

        users.setDeleted(true);

        return Map.of("message", "success");
    }

    @Transactional(readOnly = true)
    public AuthorityResDto getAuthorityProcess() {

        Users currentUser = userUtil.getCurrentUser();

        Users users = usersJpaRepository.findById(currentUser.getId()).orElseThrow(() -> new CustomException(ErrorCode.NON_EXIST_USER));

        UserAuthority userAuthority1 = users.getUserAuthority();

        return AuthorityResDto.builder()
                    .locationService(userAuthority1.getLocationService())
                    .camera(userAuthority1.getCamera())
                    .marketing(userAuthority1.getMarketing())
                    .build();

    }

    @Transactional
    public Map<String, String> modifyAuthorityProcess(String type) {

        Users currentUser = userUtil.getCurrentUser();
        Users users = usersJpaRepository.findById(currentUser.getId()).orElseThrow(() -> new CustomException(ErrorCode.NON_EXIST_USER));

        UserAuthority userAuthority = users.getUserAuthority();

        if(type.equals("locationService")){
            userAuthority.setLocationService(!userAuthority.getLocationService());
        }else if(type.equals("marketing")){
            userAuthority.setMarketing(!userAuthority.getMarketing());
        }else if(type.equals("camera")){
            userAuthority.setCamera(!userAuthority.getCamera());
        }else{
            throw new CustomException(ErrorCode.INVALID_TYPE);
        }

        return Map.of("message", "success");

    }
}
