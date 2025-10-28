package com.example.grouple.repository;

import com.example.grouple.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<Organization, Integer> {
    boolean existsById(Integer id); // 아이디 중복 체크

    boolean existsByIdAndMembers_User_Id(Integer orgId, Integer userId);

    boolean existsByIdAndOwner_Id(Integer orgId, Integer ownerId);
}