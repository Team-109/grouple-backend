package com.example.grouple.repository;

import com.example.grouple.entity.JoinRequest;
import com.example.grouple.entity.JoinRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JoinRequestRepository extends JpaRepository<JoinRequest, Integer> {

    List<JoinRequest> findAllByOrganization_Id(Integer orgId);

    Optional<JoinRequest> findByIdAndOrganization_Id(Integer reqId, Integer orgId);

    boolean existsByOrganization_IdAndUser_IdAndStatus(Integer orgId, Integer userId, JoinRequestStatus status);
}
