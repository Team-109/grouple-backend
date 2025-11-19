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

    public boolean canReadOrg(Integer orgId) {
        return orgRepo.existsByIdAndMembers_User_Id(orgId, authId());
    }
    public boolean canManageOrg(Integer orgId) {
        return orgRepo.existsByIdAndOwner_Id(orgId, authId());
    }
    private Integer authId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        if (principal instanceof AuthPrincipal userDetails) {
            return userDetails.getId();
        }
        if (principal.getClass().getName().contains("AuthPrincipal")) {
            try {
                return (Integer) principal.getClass().getMethod("getId").invoke(principal);
            } catch (Exception e) {
                throw new IllegalStateException("Cannot extract ID from AuthPrincipal proxy", e);
            }
        }
        throw new IllegalStateException("Unexpected principal type: " + principal.getClass());
    }
}