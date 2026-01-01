package com.example.Petbulance_BE.domain.hospitalWorktime.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class HospitalWorktimeKey implements Serializable {

    private Long hospitalId;

    private String dayOfWeek;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HospitalWorktimeKey)) return false;
        HospitalWorktimeKey that = (HospitalWorktimeKey) o;
        return Objects.equals(hospitalId, that.hospitalId) &&
                Objects.equals(dayOfWeek, that.dayOfWeek);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hospitalId, dayOfWeek);
    }
}
