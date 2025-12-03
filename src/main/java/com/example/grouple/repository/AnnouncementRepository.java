package com.example.grouple.repository;

import com.example.grouple.entity.Announcement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnnouncementRepository extends JpaRepository<Announcement, Integer> {
    Page<Announcement> findByOrganizationIdOrderByCreatedAtDesc(Integer organizationId, Pageable pageable);

    Page<Announcement> findByOrganizationIdAndStar(Integer organizationId, Boolean star, Pageable pageable);

    Optional<Announcement> findByIdAndOrganizationId(Integer id, Integer organizationId);

    Page<Announcement> findByOrganizationIdAndTitleContaining(Integer organizationId, String keyword, Pageable pageable);

}