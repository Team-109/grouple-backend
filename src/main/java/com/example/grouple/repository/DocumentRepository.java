package com.example.grouple.repository;

import com.example.grouple.entity.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Integer> {

    // 조직 내 모든 문서 조회 (페이징, 생성일 내림차순)
    Page<Document> findByOrganizationIdOrderByCreatedAtDesc(Integer organizationId, Pageable pageable);

    // 단일 문서 조회
    Optional<Document> findByIdAndOrganizationId(Integer documentId, Integer organizationId);



}
