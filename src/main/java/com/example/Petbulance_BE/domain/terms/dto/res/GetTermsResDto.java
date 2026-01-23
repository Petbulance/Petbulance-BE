package com.example.Petbulance_BE.domain.terms.dto.res;

import com.example.Petbulance_BE.domain.terms.enums.TermsType;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetTermsResDto {

    private Long id;

    private String title;

    private TermsType termsType;

    private Boolean required;

    private String summary;

    private String version;

}
