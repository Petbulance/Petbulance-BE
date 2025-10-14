package com.example.Petbulance_BE.global.common.controller;

import com.example.Petbulance_BE.global.common.response.GlobalResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LogoutController {

    @GetMapping("/auth/logout")
    public GlobalResponse<?> logoutProcess() {
        return GlobalResponse.success(200, "logout success");
    }

}
