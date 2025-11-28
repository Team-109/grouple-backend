package com.example.grouple.controller;

import com.example.grouple.dto.document.request.*;
import com.example.grouple.dto.document.response.*;
import com.example.grouple.service.DocumentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "07. 조직 문서")
@RestController
@RequiredArgsConstructor
@RequestMapping("/organizations/{orgId}/docs")
public class OrgDocumentController {

    private final DocumentService documentService;

    // 문서 생성
    @PostMapping
    public DocumentCreateResponse createDocument(
            @PathVariable Integer orgId,
            @RequestBody DocumentCreateRequest request
    ) {
        request.setOrganizationId(orgId);
        return documentService.createDocument(request);
    }

    // 단건 조회
    @GetMapping("/{docId}")
    public DocumentReadDetailResponse getDocument(
            @PathVariable Integer orgId,
            @PathVariable Integer docId
    ) {
        return documentService.getDocument(orgId, docId);
    }

    // 목록 조회
    @GetMapping
    public DocumentReadListResponse listDocuments(
            @PathVariable Integer orgId,
            @RequestParam(required = false) Integer userId
    ) {

        return documentService.listDocuments(orgId, userId);
    }

    // 문서 수정
    @PutMapping("/{docId}")
    public DocumentUpdateResponse updateDocument(
            @PathVariable Integer orgId,
            @PathVariable Integer docId,
            @RequestBody DocumentUpdateRequest request
    ) {
        request.setOrganizationId(orgId);
        request.setDocumentId(docId);
        return documentService.updateDocument(request);
    }

    // 문서 삭제
    @DeleteMapping("/{docId}")
    public DocumentDeleteResponse deleteDocument(
            @PathVariable Integer orgId,
            @PathVariable Integer docId
    ) {
        return documentService.deleteDocument(orgId, docId);
    }
}
