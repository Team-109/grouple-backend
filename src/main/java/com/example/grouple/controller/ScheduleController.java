package com.example.grouple.controller;

import com.example.grouple.dto.schedule.request.ScheduleCreateRequest;
import com.example.grouple.dto.schedule.request.ScheduleModifyRequest;
import com.example.grouple.dto.schedule.response.ScheduleListResponse;
import com.example.grouple.dto.schedule.response.ScheduleResponse;
import com.example.grouple.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/organizations/{orgId}/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    // 일정 목록 조회
    @GetMapping
    public ScheduleListResponse getSchedules(
            @PathVariable Integer orgId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return scheduleService.getSchedules(orgId, page, size);
    }

    // 일정 생성
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ScheduleResponse createSchedule(
            @PathVariable Integer orgId,
            @Valid @RequestBody ScheduleCreateRequest request
    ) {
        return scheduleService.createSchedule(orgId, request);
    }

    // 일정 단건 조회
    @GetMapping("/{scheduleId}")
    public ScheduleResponse getSchedule(
            @PathVariable Integer orgId,
            @PathVariable Integer scheduleId
    ) {
        return scheduleService.getSchedule(orgId, scheduleId);
    }

    // 일정 수정
    @PutMapping("/{scheduleId}")
    public ScheduleResponse modifySchedule(
            @PathVariable Integer orgId,
            @PathVariable Integer scheduleId,
            @Valid @RequestBody ScheduleModifyRequest request
    ) {
        return scheduleService.modifySchedule(orgId, scheduleId, request);
    }

    // 일정 삭제
    @DeleteMapping("/{scheduleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSchedule(
            @PathVariable Integer orgId,
            @PathVariable Integer scheduleId
    ) {
        scheduleService.deleteSchedule(orgId, scheduleId);
    }
}


