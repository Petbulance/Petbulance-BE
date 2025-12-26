package com.example.Petbulance_BE.domain.terms.dto.res;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetTermsResDto {

    private Long id;

    private String title;

    private Boolean required;

    private String summary;

    private String version;

}
