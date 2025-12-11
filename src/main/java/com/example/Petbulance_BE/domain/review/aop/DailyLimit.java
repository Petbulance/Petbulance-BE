package com.example.Petbulance_BE.domain.review.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DailyLimit {
    int maxCount() default 20;
    int minInterval() default 3;
}
