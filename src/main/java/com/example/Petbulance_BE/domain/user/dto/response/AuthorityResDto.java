package com.example.Petbulance_BE.domain.user.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorityResDto {

    private Boolean locationService;

    private Boolean marketing;

    private Boolean camera ;
}
