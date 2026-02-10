package com.example.Petbulance_BE.domain.terms.dto.req;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public class ConsentsReqDto {

    @NotNull
    private List<Long> termsId;

}
