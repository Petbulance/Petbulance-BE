package com.example.Petbulance_BE.domain.review.dto;

public class GeoDto {
    public record PointDTO(double x, double y) {}
    public record ResultDTO(PointDTO point) {}
    public record RecordDTO(String total, String current) {}
    public record PageDTO(String total, String current, String size) {}
    public record ServiceDTO(String name, String version, String operation, String time) {}

    public record ResponseDTO(
            ServiceDTO service,
            String status,
            ResultDTO result,
            RecordDTO record,
            PageDTO page
    ) {}

    public record GeoRootDTO(ResponseDTO response) {}
}