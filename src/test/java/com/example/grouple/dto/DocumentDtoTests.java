package com.example.grouple.dto;

import com.example.grouple.dto.document.request.DocumentCreateRequest;
import com.example.grouple.dto.document.request.DocumentUpdateRequest;
import com.example.grouple.dto.document.response.DocumentCreateResponse;
import com.example.grouple.dto.document.response.DocumentDeleteResponse;
import com.example.grouple.dto.document.response.DocumentReadDetailResponse;
import com.example.grouple.dto.document.response.DocumentReadListResponse;
import com.example.grouple.dto.document.response.DocumentUpdateResponse;
import com.example.grouple.entity.Document;
import com.example.grouple.entity.Organization;
import com.example.grouple.entity.User;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DocumentDtoTests {

    @Test
    void documentCreateRequest_shouldHoldValues() {
        DocumentCreateRequest req = new DocumentCreateRequest("t", "d", "f", "pdf", 10, 1);
        assertThat(req.getTitle()).isEqualTo("t");
        req.setDescription("desc");
        req.setOrganizationId(2);
        assertThat(req.getOrganizationId()).isEqualTo(2);
    }

    @Test
    void documentUpdateRequest_shouldAllowPartial() {
        DocumentUpdateRequest req = new DocumentUpdateRequest();
        req.setTitle("new");
        req.setSize(99);
        assertThat(req.getTitle()).isEqualTo("new");
        assertThat(req.getSize()).isEqualTo(99);
    }

    @Test
    void documentCreateResponse_shouldExposeFields() {
        Instant now = Instant.now();
        DocumentCreateResponse dto = new DocumentCreateResponse(
                1, "title", "desc", "file.pdf", "pdf", 100, "user", 2, now, now
        );

        assertThat(dto.getDocumentId()).isEqualTo(1);
        assertThat(dto.getTitle()).isEqualTo("title");
        assertThat(dto.getDescription()).isEqualTo("desc");
        assertThat(dto.getName()).isEqualTo("file.pdf");
        assertThat(dto.getType()).isEqualTo("pdf");
        assertThat(dto.getSize()).isEqualTo(100);
        assertThat(dto.getUsername()).isEqualTo("user");
        assertThat(dto.getOrganizationId()).isEqualTo(2);
        assertThat(dto.getCreatedAt()).isEqualTo(now);
        assertThat(dto.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void documentUpdateResponse_shouldMapFromEntity() {
        User user = new User();
        user.setId(1);
        user.setUsername("tester");
        Organization org = new Organization();
        org.setId(2);

        Document document = Document.builder()
                .title("t")
                .description("d")
                .name("f")
                .type("pdf")
                .size(10)
                .user(user)
                .organization(org)
                .build();
        document.setId(5);
        document.setCreatedAt(Instant.EPOCH);
        document.setUpdatedAt(Instant.EPOCH);

        DocumentUpdateResponse dto = DocumentUpdateResponse.from(document);

        assertThat(dto.documentId()).isEqualTo(5);
        assertThat(dto.title()).isEqualTo("t");
        assertThat(dto.name()).isEqualTo("f");
        assertThat(dto.username()).isEqualTo("tester");
        assertThat(dto.organizationId()).isEqualTo(2);
        assertThat(dto.createdAt()).isEqualTo(Instant.EPOCH);
    }

    @Test
    void documentReadDetailResponse_shouldMapFromEntity() {
        User user = new User();
        user.setId(3);
        user.setUsername("detail");
        Organization org = new Organization();
        org.setId(4);
        org.setName("Org");
        Document document = Document.builder()
                .title("t")
                .description("d")
                .name("f")
                .type("pdf")
                .size(10)
                .user(user)
                .organization(org)
                .build();
        document.setId(8);
        document.setCreatedAt(Instant.EPOCH);
        document.setUpdatedAt(Instant.EPOCH);

        DocumentReadDetailResponse dto = DocumentReadDetailResponse.from(document);

        assertThat(dto.getDocumentId()).isEqualTo(8);
        assertThat(dto.getOrganizationId()).isEqualTo(4);
        assertThat(dto.getUsername()).isEqualTo("detail");
        assertThat(dto.getCreatedAt()).isEqualTo(Instant.EPOCH);
    }

    @Test
    void documentDeleteResponse_shouldUseBuilder() {
        DocumentDeleteResponse dto = DocumentDeleteResponse.builder()
                .documentId(10)
                .message("deleted")
                .build();

        assertThat(dto.getDocumentId()).isEqualTo(10);
        assertThat(dto.getMessage()).isEqualTo("deleted");
    }

    @Test
    void documentReadListResponse_shouldExposeSummaries() {
        DocumentReadListResponse.DocumentSummary summary = DocumentReadListResponse.DocumentSummary.builder()
                .documentId(1)
                .title("title")
                .name("file.pdf")
                .username("user")
                .createdAt(Instant.EPOCH)
                .updatedAt(Instant.EPOCH)
                .build();

        DocumentReadListResponse response = DocumentReadListResponse.builder()
                .documents(List.of(summary))
                .build();

        assertThat(response.getDocuments()).containsExactly(summary);
        assertThat(response.getDocuments().getFirst().getTitle()).isEqualTo("title");
    }
}
