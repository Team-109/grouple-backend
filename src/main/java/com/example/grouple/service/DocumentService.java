package com.example.grouple.service;

import com.example.grouple.dto.document.request.*;
import com.example.grouple.dto.document.response.*;
import com.example.grouple.entity.Document;
import com.example.grouple.entity.Organization;
import com.example.grouple.entity.User;
import com.example.grouple.repository.DocumentRepository;
import com.example.grouple.repository.OrganizationRepository;
import com.example.grouple.repository.UserRepository;
import com.example.grouple.security.OrganizationAuthz;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final OrganizationAuthz orgAuthz;

    // 문서 생성
    public DocumentCreateResponse createDocument(DocumentCreateRequest request) {

        // 조직 멤버인지 확인
        if (!orgAuthz.canReadOrg(request.getOrganizationId())) {
            throw new IllegalStateException("조직 멤버가 아니어서 문서 생성 불가");
        }

        Organization org = organizationRepository.findById(request.getOrganizationId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 조직"));

        User user = userRepository.findById(orgAuthz.authId())
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        Document doc = new Document();
        doc.setTitle(request.getTitle());
        doc.setDescription(request.getDescription());
        doc.setName(request.getName());
        doc.setType(request.getType());
        doc.setSize(request.getSize());
        doc.setOrganization(org);
        doc.setUser(user);

        Document saved = documentRepository.save(doc);

        return new DocumentCreateResponse(
                saved.getId(),
                saved.getTitle(),
                saved.getDescription(),
                saved.getName(),
                saved.getType(),
                saved.getSize(),
                saved.getUser().getUsername(),
                saved.getOrganization().getId(),
                saved.getCreatedAt(),
                saved.getUpdatedAt()
        );
    }

    // 단건 조회
    public DocumentReadDetailResponse getDocument(Integer orgId, Integer docId) {

        Document doc = documentRepository.findByIdAndOrganizationId(docId, orgId)
                .orElseThrow(() -> new IllegalArgumentException("문서 없음"));

        // 조직 멤버인지 체크
        if (!orgAuthz.canReadOrg(doc.getOrganization().getId())) {
            throw new IllegalStateException("조직 멤버가 아니어서 문서 조회 불가");
        }

        return new DocumentReadDetailResponse(
                doc.getId(),
                doc.getTitle(),
                doc.getDescription(),
                doc.getName(),
                doc.getType(),
                doc.getSize(),
                doc.getUser().getUsername(),
                doc.getOrganization().getId(),
                doc.getOrganization().getName(),
                doc.getCreatedAt(),
                doc.getUpdatedAt()
        );
    }

    // 목록 조회
    public DocumentReadListResponse listDocuments(Integer orgId, Integer userId) {

        // 조직 멤버인지 체크
        if (!orgAuthz.canReadOrg(orgId)) {
            throw new IllegalStateException("조직 멤버가 아니어서 문서 목록 조회 불가");
        }

        List<Document> docs = (userId != null)
                ? documentRepository.findByOrganizationIdAndUserId(orgId, userId)
                : documentRepository.findByOrganizationId(orgId);

        List<DocumentReadListResponse.DocumentSummary> summaries = docs.stream()
                .map(d -> new DocumentReadListResponse.DocumentSummary(
                        d.getId(),
                        d.getTitle(),
                        d.getName(),
                        d.getUser().getUsername(),
                        d.getCreatedAt(),
                        d.getUpdatedAt()
                ))
                .collect(Collectors.toList());

        return new DocumentReadListResponse(summaries);
    }



    // 문서 수정
    public DocumentUpdateResponse updateDocument(DocumentUpdateRequest request) {

        Document doc = documentRepository.findById(request.getDocumentId())
                .orElseThrow(() -> new IllegalArgumentException("문서 없음"));

        if (!orgAuthz.canModifyDocument(doc.getOrganization().getId(), doc.getUser().getId())) {
            throw new IllegalStateException("문서 수정 권한 없음");
        }

        doc.setTitle(request.getTitle());
        doc.setDescription(request.getDescription());
        doc.setName(request.getName());
        doc.setType(request.getType());
        doc.setSize(request.getSize());

        Document updated = documentRepository.save(doc);

        return new DocumentUpdateResponse(
                updated.getId(),
                updated.getTitle(),
                updated.getDescription(),
                updated.getName(),
                updated.getType(),
                updated.getSize(),
                updated.getUser().getUsername(),
                updated.getOrganization().getId(),
                updated.getCreatedAt(),
                updated.getUpdatedAt()
        );
    }

    // 문서 삭제
    public DocumentDeleteResponse deleteDocument(Integer orgId, Integer docId) {

        Document doc = documentRepository.findById(docId)
                .orElseThrow(() -> new IllegalArgumentException("문서 없음"));

        // 권한 체크
        if (!orgAuthz.canModifyDocument(orgId, doc.getUser().getId())) {
            throw new IllegalStateException("문서 삭제 권한 없음");
        }

        documentRepository.delete(doc);

        return new DocumentDeleteResponse(doc.getId(), "삭제 완료");
    }

}
