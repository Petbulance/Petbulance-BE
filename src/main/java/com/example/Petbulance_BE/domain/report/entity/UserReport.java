package com.example.Petbulance_BE.domain.report.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@DiscriminatorValue("USER")
public class UserReport extends Report {

}
