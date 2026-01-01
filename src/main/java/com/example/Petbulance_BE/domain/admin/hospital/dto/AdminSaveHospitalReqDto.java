package com.example.Petbulance_BE.domain.admin.hospital.dto;

import com.example.Petbulance_BE.global.common.type.AnimalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminSaveHospitalReqDto {

    @NotBlank(message = "빈값은 입력할 수 없습니다.")
    private String hospitalName;

    @NotBlank(message = "빈값은 입력할 수 없습니다.")
    private String phoneNumber;

    @NotBlank(message = "빈값은 입력할 수 없습니다.")
    private String address;

    @NotBlank(message = "빈값은 입력할 수 없습니다.")
    private String streetAddress;

    @NotBlank(message = "빈값은 입력할 수 없습니다.")
    private String information;

    @NotNull(message = "빈값은 입력할 수 없습니다.")
    private Double lat; //위도

    @NotNull(message = "빈값은 입력할 수 없습니다.")
    private Double lon; //경도

    @NotBlank(message = "빈값은 입력할 수 없습니다.")
    private String url;

    @NotBlank(message = "빈값은 입력할 수 없습니다.")
    private String image;

    private List<String> tags = new ArrayList<>();

    private List<AnimalType> animalTypes = new ArrayList<>();

    @NotNull(message = "빈값은 입력할 수 없습니다.")
    private Boolean night;

    @NotNull(message = "빈값은 입력할 수 없습니다.")
    private Boolean twentyFour;

    private List<OperationTimeDto> operationTimes = new ArrayList<>();

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OperationTimeDto {
        @NotBlank(message = "빈값은 입력할 수 없습니다.")
        private String dayOfWeek; // "MON", "TUE" ...
        @NotNull(message = "빈값은 입력할 수 없습니다.")
        private Boolean isOpen;
        private LocalTime openTime;
        private LocalTime closeTime;
        private LocalTime startBreakTime;
        private LocalTime endBreakTime;
        private LocalTime deadLineTime;
    }
}
