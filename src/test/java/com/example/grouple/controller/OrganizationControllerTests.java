package com.example.grouple.controller;

import com.example.grouple.api.ApiResponse;
import com.example.grouple.dto.organization.request.OrgCreateRequest;
import com.example.grouple.dto.organization.request.OrgUpdateRequest;
import com.example.grouple.dto.organization.response.OrgCreateResponse;
import com.example.grouple.dto.organization.response.OrgDeleteResponse;
import com.example.grouple.dto.organization.response.OrgDetailResponse;
import com.example.grouple.dto.organization.response.OrgListResponse;
import com.example.grouple.security.AuthPrincipal;
import com.example.grouple.service.OrganizationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrganizationControllerTests {

    @Mock
    private OrganizationService organizationService;
    @InjectMocks
    private OrganizationController controller;

    @Test
    void shouldCreateOrganization() {
        OrgCreateRequest request = new OrgCreateRequest();
        request.setName("Org");
        request.setDescription("Desc");
        request.setCategory("CAT");
        request.setImage_url("logo.png");

        OrgCreateResponse response = new OrgCreateResponse(
                1, "Org", "ABC123", 10, Instant.now()
        );
        when(organizationService.createOrg(10, request)).thenReturn(response);

        ResponseEntity<?> result = controller.createOrg(new AuthPrincipal(10, "owner"), request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<?> body = (ApiResponse<?>) result.getBody();
        Assertions.assertNotNull(body);
        assertThat(body.getData()).isEqualTo(response);
        verify(organizationService).createOrg(10, request);
    }

    @Test
    void shouldReturnAllOrganizations() {
        OrgListResponse entry = OrgListResponse.builder()
                .id(1)
                .name("Org")
                .description("Desc")
                .category("CAT")
                .image_url("logo.png")
                .code("ABC123")
                .owner_id(10)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        List<OrgListResponse> expected = List.of(entry);
        when(organizationService.getAllOrgs()).thenReturn(expected);

        ResponseEntity<?> result = controller.getOrgList();

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<?> body = (ApiResponse<?>) result.getBody();
        Assertions.assertNotNull(body);
        assertThat(body.getData()).isEqualTo(expected);
        verify(organizationService).getAllOrgs();
    }

    @Test
    void shouldReturnOrganizationDetail() {
        OrgDetailResponse response = OrgDetailResponse.builder()
                                                      .id(1)
                                                      .name("Org")
                                                      .description("Desc")
                                                      .category("CAT")
                                                      .image_url("logo.png")
                                                      .code("ABC123")
                                                      .owner_id(10)
                                                      .createdAt(Instant.now())
                                                      .updatedAt(Instant.now())
                                                      .build();
        when(organizationService.getOrgById(1)).thenReturn(response);

        ResponseEntity<?> result = controller.getOrgDetail(1);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<?> body = (ApiResponse<?>) result.getBody();
        Assertions.assertNotNull(body);
        assertThat(body.getData()).isEqualTo(response);
        verify(organizationService).getOrgById(1);
    }

    @Test
    void shouldUpdateOrganization() {
        OrgUpdateRequest request = new OrgUpdateRequest();
        request.setName("New Name");
        request.setDescription("New Desc");
        request.setCategory("New Cat");
        request.setImage_url("new.png");

        OrgDetailResponse response = OrgDetailResponse.builder()
                                                      .id(1)
                                                      .name("New Name")
                                                      .description("New Desc")
                                                      .category("New Cat")
                                                      .image_url("new.png")
                                                      .code("ABC123")
                                                      .owner_id(10)
                                                      .createdAt(Instant.now())
                                                      .updatedAt(Instant.now())
                                                      .build();
        when(organizationService.updateOrg(10, 1, request)).thenReturn(response);

        ResponseEntity<?> result = controller.updateOrg(new AuthPrincipal(10, "owner"), 1, request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<?> body = (ApiResponse<?>) result.getBody();
        Assertions.assertNotNull(body);
        assertThat(body.getData()).isEqualTo(response);
        verify(organizationService).updateOrg(10, 1, request);
    }

    @Test
    void shouldDeleteOrganization() {
        OrgDeleteResponse response = OrgDeleteResponse.builder()
                .id(1)
                .code("ABC123")
                .deletedAt(Instant.now())
                .build();
        when(organizationService.deleteOrg(10, 1)).thenReturn(response);

        ResponseEntity<?> result = controller.deleteOrg(new AuthPrincipal(10, "owner"), 1);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<?> body = (ApiResponse<?>) result.getBody();
        Assertions.assertNotNull(body);
        assertThat(body.getData()).isEqualTo(response);
        verify(organizationService).deleteOrg(10, 1);
    }
}
