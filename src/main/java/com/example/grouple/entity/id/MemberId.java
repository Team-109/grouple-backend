package com.example.grouple.entity.id;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
public class MemberId implements Serializable {
    private Integer userId;
    private Integer orgId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MemberId that)) return false;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(orgId, that.orgId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, orgId);
    }
}
