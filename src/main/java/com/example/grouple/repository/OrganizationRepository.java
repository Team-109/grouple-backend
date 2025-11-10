package com.example.grouple.repository;

import com.example.grouple.entity.Organization;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<Organization, Integer> {

    boolean existsById(@org.jetbrains.annotations.NotNull Integer id); // 아이디 중복 체크

    boolean existsByIdAndMembers_User_Id(Integer orgId, Integer userId);

    boolean existsByIdAndOwner_Id(Integer orgId, Integer ownerId);

    @NotNull Optional<Organization> findById(@NotNull Integer id);

    Optional<Organization> findByCode(String code);

    List<Organization> findAllByOwner_Id(Integer userId);

    Organization getOrganizationById(Integer id);
}
