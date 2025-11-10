package com.example.grouple.service;

import com.example.grouple.dto.organization.request.OrgCreateRequest;
import com.example.grouple.dto.organization.request.OrgUpdateRequest;
import com.example.grouple.dto.organization.response.OrgCreateResponse;
import com.example.grouple.dto.organization.response.OrgDeleteResponse;
import com.example.grouple.dto.organization.response.OrgDetailResponse;
import com.example.grouple.dto.organization.response.OrgListResponse;
import com.example.grouple.entity.Organization;
import com.example.grouple.entity.User;
import com.example.grouple.repository.OrganizationRepository;
import com.example.grouple.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository orgRepo;
    private final UserRepository userRepo;
    /**
     * 아이디 중복 체크
     * 조직 생성 시 동일 ID 존재 여부 확인
     */

    @Transactional
    public OrgCreateResponse createOrg(@P("id") Integer id, OrgCreateRequest req) {
        User user = userRepo.findById(id).orElseThrow(NoSuchElementException::new);
        Organization org = new Organization();
        org.setOwner(user);
        if (req.getName() != null)
            org.setName(req.getName());
        if (req.getDescription() != null)
            org.setDescription(req.getDescription());
        if (req.getCategory() != null)
            org.setCategory(req.getCategory());
        if (req.getImage_url() != null)
            org.setImage(req.getImage_url());
        Organization saved = orgRepo.save(org);
        orgRepo.flush();
        return new OrgCreateResponse(
                saved.getId(),
                saved.getName(),
                saved.getCode(),
                saved.getOwner().getId(),
                saved.getCreatedAt()
        );
    }

    public List<OrgListResponse> getAllOrgs() {
        return orgRepo.findAll().stream()
                .map(OrgListResponse::from)
                .toList();
    }

    public List<OrgListResponse> getOrgsByOwner_Id(Integer userId) {
        return orgRepo.findAllByOwner_Id(userId).stream()
                .map(OrgListResponse::from)
                .toList();
    }

    public OrgDetailResponse getOrgById(Integer ordId) {
        Organization org = orgRepo.getOrganizationById(ordId);
        return OrgDetailResponse.from(org);
    }

    @Transactional
    public OrgDetailResponse updateOrg(Integer userId, Integer orgId, OrgUpdateRequest request) {
        Organization org = orgRepo.findById(orgId).orElseThrow(NoSuchElementException::new);
        validateOwner(userId, org);

        if (request.getName() != null) {
            org.setName(request.getName());
        }
        if (request.getDescription() != null) {
            org.setDescription(request.getDescription());
        }
        if (request.getCategory() != null) {
            org.setCategory(request.getCategory());
        }
        if (request.getImage_url() != null) {
            org.setImage(request.getImage_url());
        }

        return OrgDetailResponse.from(org);
    }

    @Transactional
    public OrgDeleteResponse deleteOrg(Integer userId, Integer orgId) {
        Organization org = orgRepo.findById(orgId).orElseThrow(NoSuchElementException::new);
        validateOwner(userId, org);

        orgRepo.delete(org);
        return OrgDeleteResponse.builder()
                .id(org.getId())
                .code(org.getCode())
                .deletedAt(Instant.now())
                .build();
    }

    private void validateOwner(Integer userId, Organization org) {
        if (org.getOwner() == null || !org.getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("조직에 대한 권한이 없습니다.");
        }
    }
}
