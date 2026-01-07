package com.example.Petbulance_BE.domain.review.dto.res;

import com.example.Petbulance_BE.domain.review.dto.GeminiApiDto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

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

    List<GeminiApiDto.Item> items;

}
