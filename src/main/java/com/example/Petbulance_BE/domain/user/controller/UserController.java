package com.example.Petbulance_BE.domain.user.controller;

import com.example.Petbulance_BE.domain.user.dto.request.*;
import com.example.Petbulance_BE.domain.user.dto.response.*;
import com.example.Petbulance_BE.domain.user.service.UserService;
import com.example.Petbulance_BE.domain.userSetting.entity.UserAuthority;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/nickname")
    public NicknameResponseDto checkNickname(@RequestParam String nickname) {
        return userService.checkNicknameProcess(nickname);
    }

    @PostMapping("/nickname")
    public NicknameSaveResponseDto saveNickname(@RequestBody @Valid NicknameSaveRequestDto RequestNickname){
        return userService.saveNicknameProcess(RequestNickname.getNickname());
    }

    @PatchMapping("/nickname")
    public NicknameUpdateResponseDto updateNickname(@RequestBody @Valid NicknameSaveRequestDto RequestNickname){
        return userService.updateNicknameProcess(RequestNickname.getNickname());
    }

    @PostMapping("/social/connect")
    public SocialConnectResponseDto connectAccount(@RequestBody SocialConnectRequestDto socialConnectRequestDto){
        return userService.socialConnectProcess(socialConnectRequestDto);
    }

    @DeleteMapping("/social/disconnect/{platform}")
    public Map<String,String> disconnectAccount(@PathVariable String platform){
        return userService.disconnectAccountProcess(platform);
    }

    @PatchMapping("/profile")
    public ProfileImageUpdateResponseDto updateProfileImage(@RequestBody ProfileImageUpdateRequestDto profileImageUpdateRequestDto){
        return userService.updateProfileImageProcess(profileImageUpdateRequestDto);
    }

    @PostMapping("/profile/success")
    public Map<String, String> checkImageUpdate (@RequestBody CheckProfileImageRequestDto checkProfileImageRequestDto){
        return userService.profileImageCheckProcess(checkProfileImageRequestDto);
    }

    @GetMapping("/me")
    public MeResponseDto myInfo (HttpServletRequest request) {
        return userService.myInfoProcess(request);
    }

    @PatchMapping("/settings/notification")
    public NotificationSettingResponseDto settingNotification (@RequestBody NotificationSettingRequestDto notificationSettingRequestDto) {
        return userService.settingNotificationProcess(notificationSettingRequestDto);
    }

    @GetMapping("/settings/notification")
    public NotificationSettingResponseDto getSettingNotification () {
        return userService.getSettingNotification();
    }

    @DeleteMapping("/users")
    public Map<String, String> userCloseAccount() {
        return userService.userCloseAccountProcess();
    }

    @GetMapping("/authority")
    public AuthorityResDto getAuthority() {
        return userService.getAuthorityProcess();
    }

}
