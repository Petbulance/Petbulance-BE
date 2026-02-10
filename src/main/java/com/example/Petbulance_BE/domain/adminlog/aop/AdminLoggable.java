package com.example.Petbulance_BE.domain.adminlog.aop;

import com.example.Petbulance_BE.domain.adminlog.type.AdminActionType;
import com.example.Petbulance_BE.domain.adminlog.type.AdminPageType;
import com.example.Petbulance_BE.domain.adminlog.type.AdminTargetType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AdminLoggable {

    AdminPageType pageType();

    AdminActionType actionType();

    AdminTargetType targetType();

    String targetId() default "";

    String description() default "";

}
