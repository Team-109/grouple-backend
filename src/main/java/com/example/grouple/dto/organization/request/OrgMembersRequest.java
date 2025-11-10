package com.example.grouple.dto.organization.request;

import lombok.Getter;
import lombok.Setter;

/**
 * Query parameters for retrieving organization members.
 */
@Getter
@Setter
public class OrgMembersRequest {
    /**
     * Optional role filter (e.g. OWNER, MANAGER, MEMBER).
     */
    private String role;
}
