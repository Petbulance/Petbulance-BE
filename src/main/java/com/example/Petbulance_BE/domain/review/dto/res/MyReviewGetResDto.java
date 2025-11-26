package com.example.Petbulance_BE.domain.review.dto.res;

import com.example.Petbulance_BE.domain.review.dto.dao.MyReviewGetDao;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MyReviewGetResDto {

    private List<MyReviewGetDao> list;

    private Long nextCursorId;

    private Boolean hasNext;


}
