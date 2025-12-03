package com.example.grouple.dto.schedule.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ScheduleListResponse {

    private List<ScheduleResponse> items;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}

