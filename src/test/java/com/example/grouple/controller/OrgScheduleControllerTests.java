package com.example.grouple.controller;


import com.example.grouple.api.ApiResponse;
import com.example.grouple.dto.schedule.request.ScheduleCreateRequest;
import com.example.grouple.dto.schedule.request.ScheduleModifyRequest;
import com.example.grouple.dto.schedule.response.ScheduleListResponse;
import com.example.grouple.dto.schedule.response.ScheduleResponse;
import com.example.grouple.security.AuthPrincipal;
import com.example.grouple.service.ScheduleService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrgScheduleControllerTests {

    @Mock
    private ScheduleService scheduleService;

    @InjectMocks
    private OrgScheduleController controller;


    @Test
    void shouldCreateSchedule() {
        Integer userId = 10;
        Integer orgId = 1;

        ScheduleCreateRequest request = new ScheduleCreateRequest();
        request.setTitle("정기 모임");
        request.setDescription("스터디 카페");

        ScheduleResponse.Author author =
                ScheduleResponse.Author.builder()
                        .id(userId)
                        .username("user")
                        .build();

        ScheduleResponse mockRes = ScheduleResponse.builder()
                .id(1)
                .title("정기 모임")
                .description("스터디 카페")
                .author(author)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(1))
                .build();

        when(scheduleService.createSchedule(userId, orgId, request))
                .thenReturn(mockRes);

        ResponseEntity<?> result =
        controller.createSchedule(new AuthPrincipal(userId, "user"), orgId, request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ApiResponse<?> body = (ApiResponse<?>) result.getBody();
        Assertions.assertNotNull(body);
        assertThat(body.getData()).isEqualTo(mockRes);

        verify(scheduleService).createSchedule(userId, orgId, request);
    }


    @Test
    void shouldGetSchedules() {
        Integer orgId = 1;
        int page = 0;
        int size = 10;

        ScheduleResponse item = ScheduleResponse.builder()
                .id(1)
                .title("모임")
                .description("설명")
                .author(null)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(1))
                .build();

        ScheduleListResponse listResponse = ScheduleListResponse.builder()
                .items(List.of(item))
                .page(page)
                .size(size)
                .totalElements(1L)
                .totalPages(1)
                .build();

        when(scheduleService.getSchedules(orgId, page, size))
                .thenReturn(listResponse);

        ResponseEntity<?> result = controller.getSchedules(orgId, page, size);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);

        ApiResponse<?> body = (ApiResponse<?>) result.getBody();
        assertThat(body.getData()).isEqualTo(listResponse);
    }


    @Test
    void shouldGetSchedule() {
        Integer orgId = 1;
        Integer scheduleId = 2;

        ScheduleResponse mockRes = ScheduleResponse.builder()
                .id(scheduleId)
                .title("테스트")
                .description("상세 설명")
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(1))
                .build();

        when(scheduleService.getSchedule(orgId, scheduleId))
                .thenReturn(mockRes);

        ResponseEntity<?> result = controller.getSchedule(orgId, scheduleId);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);

        ApiResponse<?> body = (ApiResponse<?>) result.getBody();
        Assertions.assertNotNull(body);
        assertThat(body.getData()).isEqualTo(mockRes);
    }


    @Test
    void shouldModifySchedule() {
        Integer orgId = 1;
        Integer scheduleId = 2;

        ScheduleModifyRequest request = new ScheduleModifyRequest();
        request.setTitle("수정 제목");
        request.setDescription("수정 설명");

        ScheduleResponse mockRes = ScheduleResponse.builder()
                .id(scheduleId)
                .title("수정 제목")
                .description("수정 설명")
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(1))
                .build();

        when(scheduleService.modifySchedule(orgId, scheduleId, request))
                .thenReturn(mockRes);

        ResponseEntity<?> result =
                controller.modifySchedule(orgId, scheduleId, request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);

        ApiResponse<?> body = (ApiResponse<?>) result.getBody();
        assertThat(body.getData()).isEqualTo(mockRes);
    }


    @Test
    void shouldDeleteSchedule() {
        Integer orgId = 1;
        Integer scheduleId = 2;

        ResponseEntity<?> result = controller.deleteSchedule(orgId, scheduleId);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        verify(scheduleService).deleteSchedule(orgId, scheduleId);
    }
}
