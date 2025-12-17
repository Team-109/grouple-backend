package com.example.grouple.controller;

import com.example.grouple.api.ApiResponse;
import com.example.grouple.dto.document.request.DocumentCreateRequest;
import com.example.grouple.dto.document.request.DocumentUpdateRequest;
import com.example.grouple.dto.document.response.DocumentReadDetailResponse;
import com.example.grouple.security.AuthPrincipal;
import com.example.grouple.service.DocumentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrgDocumentControllerTests {

    @Mock
    private DocumentService documentService;

    private OrgDocumentController controller;

    @Captor
    private ArgumentCaptor<PageRequest> pageRequestCaptor;

    @BeforeEach
    void setUp() {
        controller = new OrgDocumentController(documentService);
    }

    @Test
    void createDocument_shouldReturnCreatedWithResponse() {
        DocumentCreateRequest request = new DocumentCreateRequest(
                "title", "desc", "file.pdf", "pdf", 100, 1
        );
        AuthPrincipal principal = new AuthPrincipal(10, "user");
        DocumentReadDetailResponse response = DocumentReadDetailResponse.builder()
                .documentId(1)
                .title("title")
                .description("desc")
                .name("file.pdf")
                .type("pdf")
                .size(100)
                .username("user")
                .organizationId(1)
                .organizationName("Org")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        when(documentService.createDocument(1, principal.getId(), request)).thenReturn(response);

        ResponseEntity<?> result = controller.createDocument(1, request, principal);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        ApiResponse<?> body = (ApiResponse<?>) result.getBody();
        assertThat(body.getStatus()).isEqualTo("success");
        assertThat(body.getData()).isEqualTo(response);
        verify(documentService).createDocument(1, principal.getId(), request);
    }

    @Test
    void getDocument_shouldReturnDocumentDetail() {
        DocumentReadDetailResponse response = DocumentReadDetailResponse.builder()
                .documentId(5)
                .title("doc")
                .build();
        when(documentService.getDocument(1, 5)).thenReturn(response);

        ResponseEntity<?> result = controller.getDocument(1, 5);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<?> body = (ApiResponse<?>) result.getBody();
        assertThat(body.getData()).isEqualTo(response);
        verify(documentService).getDocument(1, 5);
    }

    @Test
    void listDocuments_shouldReturnPagedDocuments() {
        DocumentReadDetailResponse item = DocumentReadDetailResponse.builder()
                .documentId(2)
                .title("doc2")
                .build();
        Page<DocumentReadDetailResponse> page = new PageImpl<>(List.of(item));
        when(documentService.listDocuments(eq(3), any(PageRequest.class))).thenReturn(page);

        ResponseEntity<?> result = controller.listDocuments(3, 0, 5);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<?> body = (ApiResponse<?>) result.getBody();
        @SuppressWarnings("unchecked")
        Page<DocumentReadDetailResponse> data = (Page<DocumentReadDetailResponse>) body.getData();
        assertThat(data.getContent()).containsExactly(item);
        verify(documentService).listDocuments(eq(3), pageRequestCaptor.capture());
        PageRequest pageable = pageRequestCaptor.getValue();
        assertThat(pageable.getPageNumber()).isEqualTo(0);
        assertThat(pageable.getPageSize()).isEqualTo(5);
    }

    @Test
    void updateDocument_shouldReturnUpdatedDocument() {
        DocumentUpdateRequest request = new DocumentUpdateRequest();
        request.setTitle("updated");
        DocumentReadDetailResponse response = DocumentReadDetailResponse.builder()
                .documentId(9)
                .title("updated")
                .build();
        when(documentService.updateDocument(1, 9, request)).thenReturn(response);

        ResponseEntity<?> result = controller.updateDocument(1, 9, request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<?> body = (ApiResponse<?>) result.getBody();
        assertThat(body.getData()).isEqualTo(response);
        verify(documentService).updateDocument(1, 9, request);
    }

    @Test
    void deleteDocument_shouldReturnNoContent() {
        ResponseEntity<?> result = controller.deleteDocument(2, 15);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(documentService).deleteDocument(2, 15);
    }
}
