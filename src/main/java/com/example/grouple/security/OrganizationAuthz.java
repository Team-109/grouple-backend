package com.example.grouple.security;

import com.example.grouple.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrganizationAuthz {
    private final OrganizationRepository orgRepo;

    /**
     * 조직 멤버 여부 체크 - 조회/생성 권한
     */
    public boolean canWriteOrg(Integer orgId) {
        return orgRepo.existsByIdAndMembers_User_Id(orgId, authId());
    }

    /**
     * 조직 멤버 여부 체크 - 조회 권한
     */
    public boolean canReadOrg(Integer orgId) {
        return orgRepo.existsByIdAndMembers_User_Id(orgId, authId());
    }

    /**
     * 조직 생성자 여부 체크 - 조직 관리 권한
     */
    public boolean canManageOrg(Integer orgId) {
        return orgRepo.existsByIdAndOwner_Id(orgId, authId());
    }

    /**
     * 문서 수정/삭제 권한 체크
     * 조직 생성자 또는 문서 작성자만 가능
     */
    public boolean canModifyDocument(Integer orgId, Integer docOwnerId) {
        Integer currentUserId = authId();
        boolean isOrgOwner = orgRepo.existsByIdAndOwner_Id(orgId, currentUserId);
        boolean isDocOwner = currentUserId.equals(docOwnerId);
        return isOrgOwner || isDocOwner;
    }

    /**
     * 현재 로그인한 사용자 ID 가져오기
     */
    public Integer authId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails userDetails) {
            return userDetails.getId();
        }
        throw new IllegalStateException("Unexpected principal type: " + principal.getClass());
    }

}