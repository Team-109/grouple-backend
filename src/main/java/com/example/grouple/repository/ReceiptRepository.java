package com.example.grouple.repository;

import com.example.grouple.dto.receipt.response.ReceiptSummaryResponse;
import com.example.grouple.entity.Receipt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReceiptRepository extends JpaRepository<Receipt, Integer> {
    @Query("SELECT NEW com.example.grouple.dto.receipt.response.ReceiptSummaryResponse(" +
            "r.id, r.type, r.amount, r.category, r.date, r.image" +
            ") FROM Receipt r WHERE r.organization.id = :orgId")
    Page<ReceiptSummaryResponse> findSummariesByOrganizationId(
            @Param("orgId") Integer organizationId,
            Pageable pageable);

    @Query("SELECT r FROM Receipt r JOIN FETCH r.organization JOIN FETCH r.user WHERE r.id = :id")
    Optional<Receipt> findByIdWithRelations(@Param("id") Integer id);

    @Query("SELECT COUNT(o) > 0 FROM Receipt r " +
            "JOIN r.organization o " +
            "WHERE o.id = :orgId " +
            "AND o.owner.id = :currentUserId") // ️ o.owner.id로 User 엔티티의 ID 필드를 명시
    boolean isOrganizationOwner(@Param("orgId") Integer orgId,
                                @Param("currentUserId") Integer currentUserId);
}
