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
import org.springframework.web.server.ResponseStatusException;

@Tag(name = "04. 일정")
@RestController
@RequestMapping("/organizations/{orgId}/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    // ✅ 일정 생성
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

    // ✅ 일정 목록 조회 (페이징)
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

    // ✅ 일정 단건 조회
    @GetMapping("/{scheduleId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getSchedule(
            @PathVariable Integer orgId,
            @PathVariable Integer scheduleId
    ) {
        var res = scheduleService.getSchedule(orgId, scheduleId);
        return ResponseEntity.ok(ApiResponse.success(res));
    }

    // ✅ 일정 수정
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

    // ✅ 일정 삭제
    @DeleteMapping("/{scheduleId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteSchedule(
            @PathVariable Integer orgId,
            @PathVariable Integer scheduleId
    ) {
        scheduleService.deleteSchedule(orgId, scheduleId);
        return ResponseEntity.noContent().build();
    }

    // 🔒 AuthPrincipal에서 userId 강제 추출 (OrgController랑 동일 패턴)
    private Integer requireUserId(AuthPrincipal principal) {
        if (principal == null || principal.getId() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증 정보를 확인할 수 없습니다.");
        }
        return principal.getId();
    }
}
