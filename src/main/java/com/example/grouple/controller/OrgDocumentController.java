package com.example.grouple.controller;

import com.example.grouple.dto.document.request.*;
import com.example.grouple.dto.document.response.*;
import com.example.grouple.service.DocumentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "06. Document", description = "조직 문서 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/organizations/{organizationId}/document")
public class OrgDocumentController {

    private final DocumentService documentService;

    // 문서 생성
    @PostMapping
    public DocumentCreateResponse createDocument(
            @PathVariable Integer organizationId,
            @RequestBody DocumentCreateRequest request
    ) {
        request.setOrganizationId(organizationId);
        return documentService.createDocument(request);
    }

    // 단건 조회
    @GetMapping("/{documentId}")
    public DocumentReadDetailResponse getDocument(
            @PathVariable Integer organizationId,
            @PathVariable Integer documentId
    ) {
        DocumentReadDetailRequest request = new DocumentReadDetailRequest();
        request.setOrganizationId(organizationId);
        request.setDocumentId(documentId);

        return documentService.getDocument(request);
    }

    // 목록 조회
    @GetMapping
    public DocumentReadListResponse listDocuments(
            @PathVariable Integer organizationId,
            @RequestParam(required = false) Integer userId
    ) {
        DocumentReadListRequest request = new DocumentReadListRequest();
        request.setOrganizationId(organizationId);
        request.setUserId(userId);

        return documentService.listDocuments(request);
    }

    // 문서 수정
    @PutMapping("/{documentId}")
    public DocumentUpdateResponse updateDocument(
            @PathVariable Integer organizationId,
            @PathVariable Integer documentId,
            @RequestBody DocumentUpdateRequest request
    ) {
        request.setOrganizationId(organizationId);
        request.setDocumentId(documentId);
        return documentService.updateDocument(request);
    }

    // 문서 삭제
    @DeleteMapping("/{documentId}")
    public DocumentDeleteResponse deleteDocument(
            @PathVariable Integer organizationId,
            @PathVariable Integer documentId
    ) {
        DocumentDeleteRequest request = new DocumentDeleteRequest();
        request.setOrganizationId(organizationId);
        request.setDocumentId(documentId);

        return documentService.deleteDocument(request);
    }
}
