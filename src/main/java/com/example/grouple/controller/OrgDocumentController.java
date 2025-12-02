package com.example.grouple.controller;

import com.example.grouple.api.ApiResponse;
import com.example.grouple.dto.document.request.DocumentCreateRequest;
import com.example.grouple.dto.document.request.DocumentUpdateRequest;
import com.example.grouple.dto.document.response.DocumentReadDetailResponse;
import com.example.grouple.dto.document.response.DocumentReadListResponse;
import com.example.grouple.dto.document.response.DocumentDeleteResponse;
import com.example.grouple.security.AuthPrincipal;
import com.example.grouple.service.DocumentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "07. 조직 문서")
@RestController
@RequestMapping("/organizations/{orgId}/docs")
@PreAuthorize("isAuthenticated()")
public class OrgDocumentController {

    private final DocumentService documentService;

    public OrgDocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    // 1. 문서 생성
    @PostMapping
    public ResponseEntity<DocumentReadDetailResponse> createDocument(
            @PathVariable Integer orgId,
            @Valid @RequestBody DocumentCreateRequest request,
            @AuthenticationPrincipal AuthPrincipal principal
    ) {
        Integer currentUserId = principal.getId();
        DocumentReadDetailResponse response = documentService.createDocument(orgId, currentUserId, request);
        return ResponseEntity.ok(ApiResponse.success(response).getData());
    }

    // 2. 단일 문서 조회
    @GetMapping("/{docId}")
    public ResponseEntity<DocumentReadDetailResponse> getDocument(
            @PathVariable Integer orgId,
            @PathVariable Integer docId
    ) {
        DocumentReadDetailResponse response = documentService.getDocument(orgId, docId);
        return ResponseEntity.ok(response);
    }

    // 3. 문서 목록 조회 (페이징)
    @GetMapping
    public ResponseEntity<Page<DocumentReadDetailResponse>> listDocuments(
            @PathVariable Integer orgId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<DocumentReadDetailResponse> responsePage = documentService.listDocuments(orgId, pageable);
        return ResponseEntity.ok(responsePage);
    }

    // 4. 문서 수정
    @PutMapping("/{docId}")
    public ResponseEntity<DocumentReadDetailResponse> updateDocument(
            @PathVariable Integer orgId,
            @PathVariable Integer docId,
            @Valid @RequestBody DocumentUpdateRequest request
    ) {
        DocumentReadDetailResponse response = documentService.updateDocument(orgId, docId, request);
        return ResponseEntity.ok(response);
    }

    // 5. 문서 삭제
    @DeleteMapping("/{docId}")
    public ResponseEntity<Void> deleteDocument(
            @PathVariable Integer orgId,
            @PathVariable Integer docId
    ) {
        documentService.deleteDocument(orgId, docId);
        return ResponseEntity.noContent().build();
    }
}

