package com.example.grouple.controller;

import com.example.grouple.api.ApiResponse;
import com.example.grouple.dto.schedule.request.ScheduleCreateRequest;
import com.example.grouple.dto.schedule.request.ScheduleModifyRequest;
import com.example.grouple.security.AuthPrincipal;
import com.example.grouple.service.ScheduleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "09. 조직 일정")
@RestController
@RequestMapping("/organizations/{orgId}/schedules")
public class OrgScheduleController extends BaseController {

    private final ScheduleService scheduleService;

    public OrgScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createSchedule(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable Integer orgId,
            @Valid @RequestBody ScheduleCreateRequest request
    ) {
        Integer userId = requireUserId(principal);
        var res = scheduleService.createSchedule(userId, orgId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(res));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getSchedules(
            @PathVariable Integer orgId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        var res = scheduleService.getSchedules(orgId, page, size);
        return ResponseEntity.ok(ApiResponse.success(res));
    }

    @GetMapping("/{scheduleId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getSchedule(
            @PathVariable Integer orgId,
            @PathVariable Integer scheduleId
    ) {
        var res = scheduleService.getSchedule(orgId, scheduleId);
        return ResponseEntity.ok(ApiResponse.success(res));
    }

    @PatchMapping("/{scheduleId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> modifySchedule(
            @PathVariable Integer orgId,
            @PathVariable Integer scheduleId,
            @Valid @RequestBody ScheduleModifyRequest request
    ) {
        var res = scheduleService.modifySchedule(orgId, scheduleId, request);
        return ResponseEntity.ok(ApiResponse.success(res));
    }

    @DeleteMapping("/{scheduleId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteSchedule(
            @PathVariable Integer orgId,
            @PathVariable Integer scheduleId
    ) {
        scheduleService.deleteSchedule(orgId, scheduleId);
        return ResponseEntity.noContent().build();
    }
}
