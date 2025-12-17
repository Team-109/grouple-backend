package com.example.grouple.dto;

import com.example.grouple.dto.organization.request.OrgCreateRequest;
import com.example.grouple.dto.organization.request.OrgUpdateRequest;
import com.example.grouple.dto.organization.response.OrgCreateResponse;
import com.example.grouple.dto.organization.response.OrgDeleteResponse;
import com.example.grouple.dto.organization.response.OrgDetailResponse;
import com.example.grouple.dto.organization.response.OrgListResponse;
import com.example.grouple.entity.Organization;
import com.example.grouple.entity.User;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class OrganizationDtoTests {

    @Test
    void orgCreateRequest_shouldHoldValues() {
        OrgCreateRequest req = new OrgCreateRequest();
        req.setName("Org");
        req.setDescription("desc");
        req.setCategory("CLUB");
        req.setImage_url("img");

        assertThat(req.getName()).isEqualTo("Org");
        assertThat(req.getCategory()).isEqualTo("CLUB");
    }

    @Test
    void orgUpdateRequest_shouldHoldValues() {
        OrgUpdateRequest req = new OrgUpdateRequest();
        req.setName("Updated");
        req.setCategory("SOCIETY");
        req.setDescription("desc2");
        req.setImage_url("img2");

        assertThat(req.getDescription()).isEqualTo("desc2");
        assertThat(req.getImage_url()).isEqualTo("img2");
    }

    @Test
    void orgResponses_shouldMapFromEntity() {
        User owner = new User();
        owner.setId(1);
        Organization org = new Organization();
        org.setId(10);
        org.setOwner(owner);
        org.setName("Org");
        org.setDescription("desc");
        org.setCategory("CLUB");
        org.setImage("img");
        org.setCode("CODE");
        org.setCreatedAt(Instant.EPOCH);
        org.setUpdatedAt(Instant.EPOCH);

        OrgCreateResponse created = new OrgCreateResponse(
                org.getId(), org.getName(), org.getCode(), owner.getId(), org.getCreatedAt()
        );
        assertThat(created.getOwner_id()).isEqualTo(1);

        OrgDetailResponse detail = OrgDetailResponse.from(org);
        assertThat(detail.getId()).isEqualTo(10);
        assertThat(detail.getName()).isEqualTo("Org");

        OrgListResponse list = OrgListResponse.from(org);
        assertThat(list.getOwner_id()).isEqualTo(1);

        OrgDeleteResponse deleted = OrgDeleteResponse.builder()
                .id(10)
                .code("CODE")
                .deletedAt(Instant.EPOCH)
                .build();
        assertThat(deleted.getCode()).isEqualTo("CODE");
    }
}
