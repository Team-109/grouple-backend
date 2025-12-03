package com.example.grouple.service;

import com.example.grouple.dto.document.request.DocumentCreateRequest;
import com.example.grouple.dto.document.request.DocumentUpdateRequest;
import com.example.grouple.dto.document.response.DocumentReadDetailResponse;
import com.example.grouple.entity.Document;
import com.example.grouple.entity.Organization;
import com.example.grouple.entity.User;
import com.example.grouple.repository.DocumentRepository;
import com.example.grouple.repository.OrganizationRepository;
import com.example.grouple.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;

    // 1. 문서 생성
    @Transactional
    public DocumentReadDetailResponse createDocument(Integer organizationId, Integer userId, DocumentCreateRequest request) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new EntityNotFoundException("Organization not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Document document = new Document();
        document.setTitle(request.getTitle());
        document.setDescription(request.getDescription());
        document.setName(request.getName());
        document.setType(request.getType());
        document.setSize(request.getSize());
        document.setOrganization(organization);
        document.setUser(user);

        Document saved = documentRepository.save(document);

        return toResponse(saved);
    }

    // 2. 단일 문서 조회
    @Transactional(readOnly = true)
    public DocumentReadDetailResponse getDocument(Integer organizationId, Integer documentId) {
        Document document = documentRepository.findByIdAndOrganizationId(documentId, organizationId)
                .orElseThrow(() -> new EntityNotFoundException("Document not found"));
        return toResponse(document);
    }

    // 3. 문서 목록 조회 (페이징)
    @Transactional(readOnly = true)
    public Page<DocumentReadDetailResponse> listDocuments(Integer organizationId, Pageable pageable) {
        Page<Document> page = documentRepository.findByOrganizationIdOrderByCreatedAtDesc(organizationId, pageable);
        return page.map(this::toResponse);
    }

    // 4. 문서 업데이트
    @Transactional
    public DocumentReadDetailResponse updateDocument(Integer organizationId, Integer documentId, DocumentUpdateRequest request) {
        Document document = documentRepository.findByIdAndOrganizationId(documentId, organizationId)
                .orElseThrow(() -> new EntityNotFoundException("Document not found"));

        if (request.getTitle() != null) document.setTitle(request.getTitle());
        if (request.getDescription() != null) document.setDescription(request.getDescription());
        if (request.getName() != null) document.setName(request.getName());
        if (request.getType() != null) document.setType(request.getType());
        if (request.getSize() != null) document.setSize(request.getSize());

        Document saved = documentRepository.save(document);
        return toResponse(saved);
    }

    // 5. 문서 삭제
    @Transactional
    public void deleteDocument(Integer organizationId, Integer documentId) {
        Document document = documentRepository.findByIdAndOrganizationId(documentId, organizationId)
                .orElseThrow(() -> new EntityNotFoundException("Document not found"));
        documentRepository.delete(document);
    }

    // Entity -> DTO 변환
    private DocumentReadDetailResponse toResponse(Document document) {
        return DocumentReadDetailResponse.builder()
                .documentId(document.getId())
                .title(document.getTitle())
                .description(document.getDescription())
                .name(document.getName())
                .type(document.getType())
                .size(document.getSize())
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .username(document.getUser() != null ? document.getUser().getUsername() : null)
                .organizationId(document.getOrganization() != null ? document.getOrganization().getId() : null)
                .organizationName(document.getOrganization() != null ? document.getOrganization().getName() : null)
                .build();
    }

}
