package com.example.Petbulance_BE.domain.terms.dto.res;

import com.example.Petbulance_BE.domain.terms.enums.TermsStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TermsStatusResDto {

    private TermsStatus service;

    private TermsStatus privacy;

    private TermsStatus location;

    private TermsStatus marketing;

}
