package com.example.Petbulance_BE.domain.app.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import software.amazon.awssdk.services.s3control.model.Region;

@Getter
@AllArgsConstructor
public enum VersionType {

    APP("앱버전"),
    Region("지역버전"),
    SPECIES("동물종버전"),
    COMMUNITY("커뮤니티버전");

    private final String description;
}
