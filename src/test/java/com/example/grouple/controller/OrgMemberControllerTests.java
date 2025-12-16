package com.example.grouple.controller;

import com.example.grouple.api.ApiResponse;
import com.example.grouple.dto.organization.request.OrgMembersDeleteRequest;
import com.example.grouple.dto.organization.request.OrgMembersRequest;
import com.example.grouple.dto.organization.response.OrgMembersDeleteResponse;
import com.example.grouple.dto.organization.response.OrgMembersResponse;
import com.example.grouple.security.AuthPrincipal;
import com.example.grouple.service.MemberService;
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
class OrgMemberControllerTests {

    @Mock
    private MemberService memberService;
    @InjectMocks
    private OrgMemberController controller;

    @Test
    void shouldReturnMembers() {
        OrgMembersRequest request = new OrgMembersRequest();
        request.setRole("MEMBER");

        OrgMembersResponse.MemberInfo memberInfo = OrgMembersResponse.MemberInfo.builder()
                .memberId(5)
                .username("alpha")
                .email("alpha@example.com")
                .role("MEMBER")
                .joinedAt(Instant.now())
                .build();
        OrgMembersResponse response = OrgMembersResponse.builder()
                .organizationId(1)
                .totalCount(1)
                .members(List.of(memberInfo))
                .build();
        when(memberService.getOrgMembers(1, request)).thenReturn(response);

        ResponseEntity<?> result = controller.getMembers(1, request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<?> body = (ApiResponse<?>) result.getBody();
        Assertions.assertNotNull(body);
        assertThat(body.getData()).isEqualTo(response);
        verify(memberService).getOrgMembers(1, request);
    }

    @Test
    void shouldDeleteMember() {
        OrgMembersDeleteRequest request = new OrgMembersDeleteRequest();
        request.setReason("Left org");

        OrgMembersDeleteResponse response = OrgMembersDeleteResponse.builder()
                .organizationId(1)
                .memberId(5)
                .reason("Left org")
                .deletedAt(Instant.now())
                .build();
        when(memberService.deleteMember(10, 1, 5, request)).thenReturn(response);

        ResponseEntity<?> result = controller.deleteMember(new AuthPrincipal(10, "owner"), 1, 5, request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(result.getBody()).isNull();
        verify(memberService).deleteMember(10, 1, 5, request);
    }
}
