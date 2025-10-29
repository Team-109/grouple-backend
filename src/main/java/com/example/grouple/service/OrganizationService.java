package com.example.grouple.service;

import com.example.grouple.dto.organization.request.OrganizationCreateRequest;
import com.example.grouple.dto.organization.response.OrganizationCreateResponse;
import com.example.grouple.entity.Organization;
import com.example.grouple.entity.User;
import com.example.grouple.repository.OrganizationRepository;
import com.example.grouple.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository orgRepo;
    private final UserRepository userRepo;
    private final SecretKey jwtKey;

    /**
     * 아이디 중복 체크
     * 조직 생성 시 동일 ID 존재 여부 확인
     */
    public boolean existsById(Integer Id) {
        return orgRepo.existsById(Id);
    }

    @PreAuthorize("@OrganizationAuthz.canManageOrg(req.id)")
    @Transactional
    public OrganizationCreateResponse createOrg(@P("id") Integer id, OrganizationCreateRequest req) throws Exception {
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
        return new OrganizationCreateResponse(
                saved.getId(),
                saved.getName(),
                saved.getCode(),
                saved.getOwner().getId(),
                saved.getCreatedAt()
        );
    }
}

