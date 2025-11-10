package com.example.grouple.dto.organization.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrgMembersDeleteRequest {
    /**
     * Optional note about why the member is being removed.
     */
    private String reason;
}
