package com.example.Petbulance_BE.domain.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NotificationSettingRequestDto {

    private Boolean notificationsEnabled;

    private Boolean eventNotificationsEnabled;

    private Boolean marketingNotificationsEnabled;

}
