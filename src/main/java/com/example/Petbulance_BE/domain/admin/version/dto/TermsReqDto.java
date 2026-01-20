package com.example.Petbulance_BE.domain.admin.version.dto;

import com.example.Petbulance_BE.domain.terms.enums.TermsType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TermsReqDto {

    @NotNull
    private TermsType termsType;
    @NotBlank
    private String content;
    @NotBlank
    private String version;
    @NotNull
    private Boolean isRequired;

}
