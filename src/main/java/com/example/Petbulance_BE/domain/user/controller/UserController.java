package com.example.Petbulance_BE.domain.user.controller;

import com.example.Petbulance_BE.domain.user.dto.*;
import com.example.Petbulance_BE.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
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
    public ProfileImageUpdateResponseDto updateProfileImage(@RequestBody ProfileImageUpdateReqeustDto profileImageUpdateReqeustDto){
        return userService.updateProfileImageProcess(profileImageUpdateReqeustDto);
    }

    @GetMapping("/profile/success")
    public Map<String, String> checkImageUpdate (@RequestBody CheckProfileImageRequestDto checkProfileImageRequestDto){
        return userService.profileImageCheckProcess(checkProfileImageRequestDto);
    }

    @GetMapping("/me")
    public MeResponseDto myInfo (HttpServletRequest request) {
        return userService.myInfoProcess(request);
    }
}
