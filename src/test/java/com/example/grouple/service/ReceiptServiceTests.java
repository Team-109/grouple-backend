package com.example.grouple.service;

import com.example.grouple.common.ForbiddenException;
import com.example.grouple.common.NotFoundException;
import com.example.grouple.dto.receipt.request.ReceiptCreateRequest;
import com.example.grouple.dto.receipt.request.ReceiptUpdateRequest;
import com.example.grouple.dto.receipt.response.ReceiptCreateResponse;
import com.example.grouple.dto.receipt.response.ReceiptDetailResponse;
import com.example.grouple.dto.receipt.response.ReceiptListResponse;
import com.example.grouple.dto.receipt.response.ReceiptSummaryResponse;
import com.example.grouple.entity.Organization;
import com.example.grouple.entity.Receipt;
import com.example.grouple.entity.User;
import com.example.grouple.repository.MemberRepository;
import com.example.grouple.repository.OrganizationRepository;
import com.example.grouple.repository.ReceiptRepository;
import com.example.grouple.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReceiptServiceTests {

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private ReceiptRepository receiptRepository;
    @Mock
    private OrganizationRepository organizationRepository;
    @Mock
    private UserRepository userRepository;

    private ReceiptService receiptService;

    private Organization org;
    private User user;

    @BeforeEach
    void setUp() {
        receiptService = new ReceiptService(memberRepository, receiptRepository, organizationRepository, userRepository);
        org = new Organization();
        org.setId(1);
        user = new User();
        user.setId(2);
        user.setUsername("user");
    }

    @Test
    void getReceiptList_shouldReturnListResponse() {
        ReceiptSummaryResponse summary = new ReceiptSummaryResponse(1, "CARD", 1000, "FOOD", LocalDate.now(), null);
        Page<ReceiptSummaryResponse> page = new PageImpl<>(List.of(summary));
        when(receiptRepository.findSummariesByOrganizationId(eq(1), any(PageRequest.class))).thenReturn(page);

        ReceiptListResponse response = receiptService.getReceiptList(1, PageRequest.of(0, 10));

        assertThat(response.getData()).containsExactly(summary);
        assertThat(response.getTotalCount()).isEqualTo(1);
    }

    @Test
    void createReceipt_shouldPersistAndReturnResponse() {
        ReceiptCreateRequest request = new ReceiptCreateRequest("CARD", 1000, "FOOD", LocalDate.now(), null, "memo");
        when(organizationRepository.findById(1)).thenReturn(Optional.of(org));
        when(userRepository.findById(2)).thenReturn(Optional.of(user));
        when(receiptRepository.save(any(Receipt.class))).thenAnswer(invocation -> {
            Receipt r = invocation.getArgument(0);
            r.setId(10);
            return r;
        });

        ReceiptCreateResponse response = receiptService.createReceipt(1, 2, request);

        assertThat(response.getReceiptId()).isEqualTo(10);
        verify(receiptRepository).save(any(Receipt.class));
    }

    @Test
    void viewReceipt_shouldReturnDetailWhenMember() {
        Receipt receipt = buildReceipt(11);
        when(receiptRepository.findByIdWithRelations(11)).thenReturn(Optional.of(receipt));
        when(receiptRepository.isOrganizationOwner(1, 2)).thenReturn(false);
        when(memberRepository.existsById_OrgIdAndId_UserId(1, 2)).thenReturn(true);

        ReceiptDetailResponse response = receiptService.viewReceipt(1, 11, 2);

        assertThat(response.id()).isEqualTo(11);
        assertThat(response.recordedByUserId()).isEqualTo(2);
    }

    @Test
    void viewReceipt_shouldThrowForbiddenForNonMember() {
        Receipt receipt = buildReceipt(12);
        when(receiptRepository.findByIdWithRelations(12)).thenReturn(Optional.of(receipt));
        when(receiptRepository.isOrganizationOwner(1, 2)).thenReturn(false);
        when(memberRepository.existsById_OrgIdAndId_UserId(1, 2)).thenReturn(false);

        assertThatThrownBy(() -> receiptService.viewReceipt(1, 12, 2))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void updateReceipt_shouldApplyChangesForOwnerMember() {
        Receipt receipt = buildReceipt(13);
        when(receiptRepository.findByIdWithRelations(13)).thenReturn(Optional.of(receipt));
        when(receiptRepository.isOrganizationOwner(1, 2)).thenReturn(false);
        when(memberRepository.existsById_OrgIdAndId_UserId(1, 2)).thenReturn(true);
        ReceiptUpdateRequest request = new ReceiptUpdateRequest("CASH", 2000, "ETC", "updated", LocalDate.now(), "img");

        var response = receiptService.updateReceipt(1, 13, 2, request);

        assertThat(response.receiptId()).isEqualTo(13);
        assertThat(receipt.getAmount()).isEqualTo(2000);
        assertThat(receipt.getCategory()).isEqualTo("ETC");
        // updateReceipt mutates the managed entity, repository.save is not invoked explicitly
        verify(receiptRepository).findByIdWithRelations(13);
    }

    @Test
    void deleteReceipt_shouldRemoveWhenOwnerMember() {
        Receipt receipt = buildReceipt(14);
        when(receiptRepository.findByIdWithRelations(14)).thenReturn(Optional.of(receipt));
        when(receiptRepository.isOrganizationOwner(1, 2)).thenReturn(false);
        when(memberRepository.existsById_OrgIdAndId_UserId(1, 2)).thenReturn(true);

        receiptService.deleteReceipt(1, 14, 2);

        verify(receiptRepository).delete(receipt);
    }

    @Test
    void deleteReceipt_shouldThrowWhenOrgMismatch() {
        Receipt receipt = buildReceipt(15);
        receipt.getOrganization().setId(99);
        when(receiptRepository.findByIdWithRelations(15)).thenReturn(Optional.of(receipt));

        assertThatThrownBy(() -> receiptService.deleteReceipt(1, 15, 2))
                .isInstanceOf(NotFoundException.class);
    }

    private Receipt buildReceipt(int id) {
        Receipt receipt = Receipt.builder()
                .type("CARD")
                .amount(1000)
                .category("FOOD")
                .description("desc")
                .date(LocalDate.now())
                .image("img")
                .organization(org)
                .user(user)
                .build();
        receipt.setId(id);
        return receipt;
    }
}
