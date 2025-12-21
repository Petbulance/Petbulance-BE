package com.example.Petbulance_BE.domain.report.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@DiscriminatorValue("POST")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostReport extends Report {
}

