package com.example.Petbulance_BE.domain.user.dto.response;

import com.example.Petbulance_BE.domain.userSetting.entity.UserSetting;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSettingResponseDto {

    private Boolean notificationsEnabled;

    private Boolean eventNotificationsEnabled;

    private Boolean marketingNotificationsEnabled;

}
