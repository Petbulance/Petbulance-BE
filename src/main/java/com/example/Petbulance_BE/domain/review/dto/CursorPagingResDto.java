package com.example.Petbulance_BE.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CursorPagingResDto<T> {

    List<T> list;

    Long nextCursorId;

    Boolean hasNext;

}
