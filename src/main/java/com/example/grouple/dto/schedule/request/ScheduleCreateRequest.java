package com.example.grouple.dto.schedule.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ScheduleCreateRequest {

    @NotBlank
    private String title;

    private String description;

    @NotNull
    @JsonProperty("start_time")   // JSON 필드명: start_time
    private LocalDateTime startTime;

    @NotNull
    @JsonProperty("end_time")     // JSON 필드명: end_time
    private LocalDateTime endTime;
}

