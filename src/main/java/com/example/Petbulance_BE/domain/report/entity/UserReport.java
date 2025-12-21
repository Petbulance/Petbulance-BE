package com.example.Petbulance_BE.domain.report.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@DiscriminatorValue("USER")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserReport extends Report {

}
