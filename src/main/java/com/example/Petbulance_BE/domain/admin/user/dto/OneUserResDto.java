package com.example.Petbulance_BE.domain.admin.user.dto;

import com.example.Petbulance_BE.domain.user.type.SanctionType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OneUserResDto {

    private String signUpPath;

    private LocalDateTime signUpTime;

    private Integer warningCount;

    private String userStatus;

    private List<ReportInfo> reportInfo;

    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReportInfo{

        private LocalDateTime reportTime;

        private SanctionType actionContent;

        private String actionReason;

        private String adminId;

    }

}
