package com.example.grouple.repository;

import com.example.grouple.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Integer> {

    List<Document> findByUserId(Integer userId);

    List<Document> findByOrganizationId(Integer organizationId);

    List<Document> findByOrganizationIdAndUserId(Integer organizationId, Integer userId);

    Optional<Document> findByIdAndOrganizationId(Integer documentId, Integer organizationId);
}

