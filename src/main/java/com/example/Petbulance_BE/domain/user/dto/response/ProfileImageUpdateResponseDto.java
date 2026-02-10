package com.example.Petbulance_BE.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileImageUpdateResponseDto {

    private String preSignedUrl;

    private String imageUrl;

    private String saveId;
}
