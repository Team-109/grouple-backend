package com.example.grouple.repository;

import com.example.grouple.entity.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

    // 조직별 일정 목록
    Page<Schedule> findByOrganization_Id(Integer organizationId, Pageable pageable);

    // 조직별 일정 단건
    Optional<Schedule> findByIdAndOrganization_Id(Integer id, Integer organizationId);
}
