package com.example.Petbulance_BE.domain.review.dto.res;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceiptResDto {

    private Long hospitalId;

    private String hospitalName;

    private LocalDateTime visitDateTime;

    private Long price;

}
