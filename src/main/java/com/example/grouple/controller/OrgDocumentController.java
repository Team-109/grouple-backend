package com.example.grouple.controller;

import com.example.grouple.api.ApiResponse;
import com.example.grouple.dto.document.request.DocumentCreateRequest;
import com.example.grouple.dto.document.request.DocumentUpdateRequest;
import com.example.grouple.dto.document.response.DocumentReadDetailResponse;
import com.example.grouple.security.AuthPrincipal;
import com.example.grouple.service.DocumentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "07. 조직 문서")
@RestController
@RequestMapping("/organizations/{orgId}/documents")
public class OrgDocumentController extends BaseController {

    private final DocumentService documentService;

    public OrgDocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createDocument(
            @PathVariable Integer orgId,
            @Valid @RequestBody DocumentCreateRequest request,
            @AuthenticationPrincipal AuthPrincipal principal
    ) {
        Integer currentUserId = requireUserId(principal);
        DocumentReadDetailResponse response = documentService.createDocument(orgId, currentUserId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @GetMapping("/{docId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getDocument(
            @PathVariable Integer orgId,
            @PathVariable Integer docId
    ) {
        DocumentReadDetailResponse response = documentService.getDocument(orgId, docId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> listDocuments(
            @PathVariable Integer orgId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<DocumentReadDetailResponse> responsePage = documentService.listDocuments(orgId, pageable);
        return ResponseEntity.ok(ApiResponse.success(responsePage));
    }

    @PutMapping("/{docId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateDocument(
            @PathVariable Integer orgId,
            @PathVariable Integer docId,
            @Valid @RequestBody DocumentUpdateRequest request
    ) {
        DocumentReadDetailResponse response = documentService.updateDocument(orgId, docId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{docId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteDocument(
            @PathVariable Integer orgId,
            @PathVariable Integer docId
    ) {
        documentService.deleteDocument(orgId, docId);
        return ResponseEntity.noContent().build();
    }
}

