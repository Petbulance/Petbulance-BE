package com.example.Petbulance_BE.domain.admin.version.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TermsApplyReqDto {

    @NotBlank
    private Long id;

}
