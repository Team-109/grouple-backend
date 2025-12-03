package com.example.grouple.service;

import com.example.grouple.dto.schedule.request.ScheduleCreateRequest;
import com.example.grouple.dto.schedule.request.ScheduleModifyRequest;
import com.example.grouple.dto.schedule.response.ScheduleListResponse;
import com.example.grouple.dto.schedule.response.ScheduleResponse;
import com.example.grouple.entity.Organization;
import com.example.grouple.entity.Schedule;
import com.example.grouple.entity.User;
import com.example.grouple.repository.ScheduleRepository;
import com.example.grouple.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    /**
     * 일정 생성
     * - userId : 요청 보낸 사용자 ID (조직 서비스랑 패턴 맞춤)
     * - orgId  : 일정이 속한 조직 ID
     */
    public ScheduleResponse createSchedule(@P("userId") Integer userId,
                                           Integer orgId,
                                           ScheduleCreateRequest request) {

        // 1) 유효성 검사
        if (request.getEndTime().isBefore(request.getStartTime())) {
            throw new IllegalArgumentException("end_time must be after start_time");
        }

        // 2) 사용자 조회 (조직 서비스랑 똑같이 userRepo에서 findById)
        User user = userRepository.findById(userId)
                .orElseThrow(NoSuchElementException::new);

        // 3) 엔티티 생성
        Schedule schedule = new Schedule();
        schedule.setTitle(request.getTitle());
        schedule.setDescription(request.getDescription());
        schedule.setStartTime(request.getStartTime());
        schedule.setEndTime(request.getEndTime());
        schedule.setUser(user);

        // 조직은 id만 세팅해서 연관 관계만 맺어줌
        Organization organization = new Organization();
        organization.setId(orgId);
        schedule.setOrganization(organization);

        // 4) 저장
        Schedule saved = scheduleRepository.save(schedule);

        // 5) 응답 DTO 변환
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
                                .username(s.getUser().getUsername())
                                .build()
                )
                .build();
    }
}
