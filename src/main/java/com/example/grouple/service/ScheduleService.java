package com.example.grouple.service;

import com.example.grouple.dto.schedule.request.ScheduleCreateRequest;
import com.example.grouple.dto.schedule.request.ScheduleModifyRequest;
import com.example.grouple.dto.schedule.response.ScheduleListResponse;
import com.example.grouple.dto.schedule.response.ScheduleResponse;
import com.example.grouple.entity.Organization;
import com.example.grouple.entity.Schedule;
import com.example.grouple.entity.User;
import com.example.grouple.repository.ScheduleRepository;
import com.example.grouple.repository.UserRepository; // 없으면 나중에 만들거나 주석 처리해도 됨
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;   // 없으면 일단 주석 처리해두고 알려줘!

    // 일정 생성
    public ScheduleResponse createSchedule(Integer orgId, ScheduleCreateRequest request) {

        if (request.getEndTime().isBefore(request.getStartTime())) {
            throw new IllegalArgumentException("end_time must be after start_time");
        }

        Schedule schedule = new Schedule();
        schedule.setTitle(request.getTitle());
        schedule.setDescription(request.getDescription());
        schedule.setStartTime(request.getStartTime());
        schedule.setEndTime(request.getEndTime());

        // TODO: 나중에 JWT에서 로그인 유저 꺼내서 사용
        User user = userRepository.findById(1)
                .orElseThrow(() -> new IllegalStateException("test user not found"));
        schedule.setUser(user);

        // 조직은 id만 꽂아서 연관관계 설정
        Organization organization = new Organization();
        organization.setId(orgId);
        schedule.setOrganization(organization);

        Schedule saved = scheduleRepository.save(schedule);
        return toResponse(saved);
    }

    // 일정 목록 조회
    @Transactional(readOnly = true)
    public ScheduleListResponse getSchedules(Integer orgId, int page, int size) {
        Page<Schedule> result = scheduleRepository.findByOrganization_Id(
                orgId,
                PageRequest.of(page, size, Sort.by("startTime").descending())
        );

        return ScheduleListResponse.builder()
                .items(result.map(this::toResponse).getContent())
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .build();
    }

    // 일정 단건 조회
    @Transactional(readOnly = true)
    public ScheduleResponse getSchedule(Integer orgId, Integer scheduleId) {
        Schedule schedule = scheduleRepository.findByIdAndOrganization_Id(scheduleId, orgId)
                .orElseThrow(() -> new IllegalArgumentException("schedule not found"));
        return toResponse(schedule);
    }

    // 일정 수정
    public ScheduleResponse modifySchedule(Integer orgId, Integer scheduleId, ScheduleModifyRequest request) {
        Schedule schedule = scheduleRepository.findByIdAndOrganization_Id(scheduleId, orgId)
                .orElseThrow(() -> new IllegalArgumentException("schedule not found"));

        if (request.getEndTime().isBefore(request.getStartTime())) {
            throw new IllegalArgumentException("end_time must be after start_time");
        }

        schedule.setTitle(request.getTitle());
        schedule.setDescription(request.getDescription());
        schedule.setStartTime(request.getStartTime());
        schedule.setEndTime(request.getEndTime());

        return toResponse(schedule);
    }

    // 일정 삭제
    public void deleteSchedule(Integer orgId, Integer scheduleId) {
        Schedule schedule = scheduleRepository.findByIdAndOrganization_Id(scheduleId, orgId)
                .orElseThrow(() -> new IllegalArgumentException("schedule not found"));
        scheduleRepository.delete(schedule);
    }

    // 엔티티 → DTO 변환
    private ScheduleResponse toResponse(Schedule s) {
        return ScheduleResponse.builder()
                .id(s.getId())
                .title(s.getTitle())
                .description(s.getDescription())
                .startTime(s.getStartTime())
                .endTime(s.getEndTime())
                .author(
                        ScheduleResponse.Author.builder()
                                .id(s.getUser().getId())
                                .username(s.getUser().getUsername()) // User 엔티티 필드명에 맞게 수정
                                .build()
                )
                .build();
    }
}
