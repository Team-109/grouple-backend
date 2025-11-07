package com.example.grouple.controller;

import com.example.grouple.api.ApiResponse;
import com.example.grouple.dto.organization.request.OrganizationCreateRequest;
import com.example.grouple.dto.organization.response.OrganizationCreateResponse;
import com.example.grouple.service.AuthService;
import com.example.grouple.service.OrganizationService;
import com.example.grouple.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrganizationControllerTests {

    @Mock
    private OrganizationService organizationService;
    @Mock
    private AuthService authService;
    @Mock
    private UserService userService;
    @InjectMocks
    private OrganizationController controller;

    @Test
    void shouldCreateOrganization() throws Exception {
        OrganizationCreateRequest request = new OrganizationCreateRequest();
        request.setName("Org");
        request.setDescription("Desc");
        request.setCategory("CAT");
        request.setImage_url("logo.png");

        OrganizationCreateResponse response = new OrganizationCreateResponse(
                1, "Org", "ABC123", 10, Instant.now()
        );
        when(organizationService.createOrg(10, request)).thenReturn(response);

        ResponseEntity<?> result = controller.createOrg(10, request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<?> body = (ApiResponse<?>) result.getBody();
        assertThat(body.getData()).isEqualTo(response);
        verify(organizationService).createOrg(10, request);
    }
}
