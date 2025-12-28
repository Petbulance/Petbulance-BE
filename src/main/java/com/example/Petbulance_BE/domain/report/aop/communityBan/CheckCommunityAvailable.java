package com.example.Petbulance_BE.domain.report.aop.communityBan;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CheckCommunityAvailable {
}
