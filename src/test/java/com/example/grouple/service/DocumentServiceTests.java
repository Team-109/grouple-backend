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

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTests {

    @Mock
    private DocumentRepository documentRepository;
    @Mock
    private OrganizationRepository organizationRepository;
    @Mock
    private UserRepository userRepository;

    private DocumentService documentService;

    @Captor
    private ArgumentCaptor<Document> documentCaptor;

    private Organization org;
    private User user;

    @BeforeEach
    void setUp() {
        documentService = new DocumentService(documentRepository, organizationRepository, userRepository);
        org = new Organization();
        org.setId(1);
        org.setName("Org");
        user = new User();
        user.setId(2);
        user.setUsername("user");
    }

    @Test
    void createDocument_shouldPersistAndReturnResponse() {
        DocumentCreateRequest request = new DocumentCreateRequest("title", "desc", "file.pdf", "pdf", 100, 1);
        when(organizationRepository.findById(1)).thenReturn(Optional.of(org));
        when(userRepository.findById(2)).thenReturn(Optional.of(user));
        when(documentRepository.save(any(Document.class))).thenAnswer(invocation -> {
            Document doc = invocation.getArgument(0);
            doc.setId(10);
            return doc;
        });

        DocumentReadDetailResponse response = documentService.createDocument(1, 2, request);

        assertThat(response.getDocumentId()).isEqualTo(10);
        assertThat(response.getOrganizationId()).isEqualTo(1);
        assertThat(response.getUsername()).isEqualTo("user");
        verify(documentRepository).save(documentCaptor.capture());
        Document saved = documentCaptor.getValue();
        assertThat(saved.getTitle()).isEqualTo("title");
        assertThat(saved.getUser()).isSameAs(user);
        assertThat(saved.getOrganization()).isSameAs(org);
    }

    @Test
    void getDocument_shouldReturnMappedDto() {
        Document doc = new Document();
        doc.setId(3);
        doc.setTitle("t");
        doc.setOrganization(org);
        doc.setUser(user);
        doc.setCreatedAt(Instant.EPOCH);
        doc.setUpdatedAt(Instant.EPOCH);
        when(documentRepository.findByIdAndOrganizationId(3, 1)).thenReturn(Optional.of(doc));

        DocumentReadDetailResponse response = documentService.getDocument(1, 3);

        assertThat(response.getDocumentId()).isEqualTo(3);
        assertThat(response.getOrganizationId()).isEqualTo(1);
        assertThat(response.getUsername()).isEqualTo("user");
    }

    @Test
    void getDocument_shouldThrowWhenNotFound() {
        when(documentRepository.findByIdAndOrganizationId(9, 1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> documentService.getDocument(1, 9))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void listDocuments_shouldReturnPageMapping() {
        Document doc = new Document();
        doc.setId(4);
        doc.setTitle("page");
        doc.setOrganization(org);
        doc.setUser(user);
        Page<Document> page = new PageImpl<>(java.util.List.of(doc));
        when(documentRepository.findByOrganizationIdOrderByCreatedAtDesc(eq(1), any(PageRequest.class))).thenReturn(page);

        Page<DocumentReadDetailResponse> result = documentService.listDocuments(1, PageRequest.of(0, 5));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getDocumentId()).isEqualTo(4);
    }

    @Test
    void updateDocument_shouldApplyChanges() {
        Document existing = new Document();
        existing.setId(6);
        existing.setTitle("old");
        when(documentRepository.findByIdAndOrganizationId(6, 1)).thenReturn(Optional.of(existing));
        when(documentRepository.save(any(Document.class))).thenAnswer(invocation -> invocation.getArgument(0));
        DocumentUpdateRequest request = new DocumentUpdateRequest();
        request.setTitle("new title");
        request.setSize(999);

        DocumentReadDetailResponse response = documentService.updateDocument(1, 6, request);

        assertThat(response.getTitle()).isEqualTo("new title");
        assertThat(response.getSize()).isEqualTo(999);
        verify(documentRepository).save(existing);
    }

    @Test
    void deleteDocument_shouldRemoveEntity() {
        Document doc = new Document();
        doc.setId(8);
        when(documentRepository.findByIdAndOrganizationId(8, 1)).thenReturn(Optional.of(doc));

        documentService.deleteDocument(1, 8);

        verify(documentRepository).delete(doc);
    }
}
