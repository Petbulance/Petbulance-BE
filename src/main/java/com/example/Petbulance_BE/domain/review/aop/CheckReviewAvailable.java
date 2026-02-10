package com.example.Petbulance_BE.domain.review.aop;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CheckReviewAvailable {
}
