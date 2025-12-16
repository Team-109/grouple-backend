package com.example.grouple.controller;

import com.example.grouple.dto.receipt.request.ReceiptCreateRequest;
import com.example.grouple.dto.receipt.request.ReceiptUpdateRequest;
import com.example.grouple.dto.receipt.response.ReceiptDetailResponse;
import com.example.grouple.dto.receipt.response.ReceiptSummaryResponse;
import com.example.grouple.entity.Organization;
import com.example.grouple.entity.Receipt;
import com.example.grouple.entity.User;
import com.example.grouple.repository.MemberRepository;
import com.example.grouple.repository.OrganizationRepository;
import com.example.grouple.repository.ReceiptRepository;
import com.example.grouple.repository.UserRepository;
import com.example.grouple.service.ReceiptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import com.example.grouple.common.ForbiddenException;
import com.example.grouple.common.NotFoundException;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReceiptControllerTests {

    @Mock private MemberRepository memberRepository;
    @Mock private ReceiptRepository receiptRepository;
    @Mock private OrganizationRepository organizationRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private ReceiptService receiptService;

    private final Integer MOCK_USER_ID = 10;
    private final Integer MOCK_ORG_ID = 1;
    private final Integer MOCK_RECEIPT_ID = 5;

    private Organization mockOrganization;
    private User mockUser;
    private Receipt mockReceipt;

    @BeforeEach
    void setUp() {
        // 테스트 전 Mock 엔티티 초기화
        mockOrganization = new Organization();
        mockOrganization.setId(MOCK_ORG_ID);
        mockOrganization.setOwner(mockUser); // Organization 엔티티의 owner 필드 설정

        mockUser = new User();
        mockUser.setId(MOCK_USER_ID);

        mockReceipt = Receipt.builder()
                .type("식비")
                .amount(10000)
                .category("식사")
                .date(LocalDate.now())
                .organization(mockOrganization)
                .user(mockUser)
                .build();
        mockReceipt.setId(MOCK_RECEIPT_ID);
    }

    // ---------------------------------------------------------------------------------------------------
    // 1. 목록 조회 테스트 (getReceiptList)
    // ---------------------------------------------------------------------------------------------------

    @Test
    @DisplayName("가계부 목록 조회 성공")
    void shouldGetReceiptListSuccess() {
        // GIVEN
        Pageable pageable = Pageable.unpaged();
        // Repository가 Page 객체를 반환하도록 Mocking
        Page<ReceiptSummaryResponse> mockPage = new PageImpl<>(List.of());

        when(receiptRepository.findSummariesByOrganizationId(
                eq(MOCK_ORG_ID), any(Pageable.class))).thenReturn(mockPage);

        // WHEN
        receiptService.getReceiptList(MOCK_ORG_ID, pageable);

        // THEN
        // Repository 호출이 정확히 한 번 발생했는지 검증
        verify(receiptRepository, times(1)).findSummariesByOrganizationId(eq(MOCK_ORG_ID), any(Pageable.class));
    }

    // ---------------------------------------------------------------------------------------------------
    // 2. 생성 테스트 (createReceipt)
    // ---------------------------------------------------------------------------------------------------

    @Test
    @DisplayName("가계부 항목 생성 성공")
    void shouldCreateReceiptSuccess() {
        // GIVEN
        ReceiptCreateRequest request = new ReceiptCreateRequest(
                "식비", 10000, "식사", LocalDate.now(), "img.png", "점심");

        // Organization/User Repository가 엔티티를 찾도록 Mocking
        when(organizationRepository.findById(MOCK_ORG_ID)).thenReturn(Optional.of(mockOrganization));
        when(userRepository.findById(MOCK_USER_ID)).thenReturn(Optional.of(mockUser));

        // save() 호출 시, ID가 설정된 엔티티를 반환하도록 Mocking
        when(receiptRepository.save(any(Receipt.class))).thenReturn(mockReceipt);

        // WHEN
        receiptService.createReceipt(MOCK_ORG_ID, MOCK_USER_ID, request);

        // THEN
        verify(organizationRepository).findById(MOCK_ORG_ID);
        verify(userRepository).findById(MOCK_USER_ID);
        verify(receiptRepository).save(any(Receipt.class));
    }

    @Test
    @DisplayName("생성 실패 - 조직 ID를 찾을 수 없음 (404)")
    void shouldThrowWhenOrganizationNotFoundOnCreate() {
        // GIVEN
        ReceiptCreateRequest request = new ReceiptCreateRequest(
                "식비", 10000, "식사", LocalDate.now(), "img.png", "점심");

        // Organization Repository가 Optional.empty() 반환하도록 Mocking
        when(organizationRepository.findById(MOCK_ORG_ID)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(NotFoundException.class, () ->
                receiptService.createReceipt(MOCK_ORG_ID, MOCK_USER_ID, request));

        // User Repository는 호출되지 않았는지 검증
        verify(userRepository, never()).findById(any());
    }

    // ---------------------------------------------------------------------------------------------------
    // 3. 상세 조회 테스트 (viewReceipt)
    // ---------------------------------------------------------------------------------------------------

    @Test
    @DisplayName("상세 조회 성공 - 일반 멤버 권한")
    void shouldViewReceiptSuccessAsMember() {
        // GIVEN
        when(receiptRepository.findByIdWithRelations(MOCK_RECEIPT_ID)).thenReturn(Optional.of(mockReceipt));
        // 인가 검증: 일반 멤버로 설정 (isOwner=false, isMember=true)
        when(receiptRepository.isOrganizationOwner(MOCK_ORG_ID, MOCK_USER_ID)).thenReturn(false);
        when(memberRepository.existsById_OrgIdAndId_UserId(MOCK_ORG_ID, MOCK_USER_ID)).thenReturn(true);

        // WHEN
        ReceiptDetailResponse response = receiptService.viewReceipt(MOCK_ORG_ID, MOCK_RECEIPT_ID, MOCK_USER_ID);

        // THEN
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(MOCK_RECEIPT_ID);
        // Repository 및 Member/Owner 확인 로직 호출 검증
        verify(memberRepository).existsById_OrgIdAndId_UserId(MOCK_ORG_ID, MOCK_USER_ID);
    }

    @Test
    @DisplayName("상세 조회 실패 - 권한 없음 (403)")
    void shouldThrowAccessDeniedWhenNotMemberOrOwner() {
        // GIVEN
        when(receiptRepository.findByIdWithRelations(MOCK_RECEIPT_ID)).thenReturn(Optional.of(mockReceipt));
        // 인가 검증: 둘 다 false로 설정
        when(receiptRepository.isOrganizationOwner(MOCK_ORG_ID, MOCK_USER_ID)).thenReturn(false);
        when(memberRepository.existsById_OrgIdAndId_UserId(MOCK_ORG_ID, MOCK_USER_ID)).thenReturn(false);

        // WHEN & THEN
        assertThrows(ForbiddenException.class, () ->
                receiptService.viewReceipt(MOCK_ORG_ID, MOCK_RECEIPT_ID, MOCK_USER_ID));
    }

    // ---------------------------------------------------------------------------------------------------
    // 4. 수정 테스트 (updateReceipt)
    // ---------------------------------------------------------------------------------------------------

    @Test
    @DisplayName("수정 성공 - 기록자 및 멤버 권한 일치")
    void shouldUpdateReceiptSuccess() {
        // GIVEN
        ReceiptUpdateRequest request = new ReceiptUpdateRequest(
                "교통", 5000, "택시", "집으로", LocalDate.now(), "new_img.png");

        // 1. 조회 Mocking: ID가 일치하고 기록자가 본인인 Receipt 반환
        when(receiptRepository.findByIdWithRelations(MOCK_RECEIPT_ID)).thenReturn(Optional.of(mockReceipt));
        // 2. 인가 Mocking: 멤버 권한 있음
        when(receiptRepository.isOrganizationOwner(MOCK_ORG_ID, MOCK_USER_ID)).thenReturn(false);
        when(memberRepository.existsById_OrgIdAndId_UserId(MOCK_ORG_ID, MOCK_USER_ID)).thenReturn(true);
        // (4. 기록자 확인: mockReceipt.getUser().getId() == MOCK_USER_ID 이므로 통과)

        // WHEN
        receiptService.updateReceipt(MOCK_ORG_ID, MOCK_RECEIPT_ID, MOCK_USER_ID, request);

        // THEN
        // 엔티티가 수정되었는지 확인 (Dirty Checking)
        assertThat(mockReceipt.getAmount()).isEqualTo(request.amount());
        assertThat(mockReceipt.getCategory()).isEqualTo(request.category());
        // Repository.save()는 @Transactional로 인해 호출되지 않음. Dirty Checking 검증으로 대체.
        verify(receiptRepository).findByIdWithRelations(MOCK_RECEIPT_ID);
    }

    @Test
    @DisplayName("수정 실패 - 기록자가 아님 (403)")
    void shouldThrowAccessDeniedWhenNotRecorder() {
        // GIVEN
        Integer otherUserId = 99;
        ReceiptUpdateRequest request = new ReceiptUpdateRequest(
                "교통", 5000, "택시", "집으로", LocalDate.now(), "new_img.png");

        // 1. 조회 Mocking: 본인이 기록한 항목이 아님 (mockReceipt는 MOCK_USER_ID가 기록)
        when(receiptRepository.findByIdWithRelations(MOCK_RECEIPT_ID)).thenReturn(Optional.of(mockReceipt));
        // 2. 인가 Mocking: 멤버 권한은 있음
        when(receiptRepository.isOrganizationOwner(MOCK_ORG_ID, otherUserId)).thenReturn(false);
        when(memberRepository.existsById_OrgIdAndId_UserId(MOCK_ORG_ID, otherUserId)).thenReturn(true);

        // WHEN & THEN
        // otherUserId로 수정 시도
        assertThrows(ForbiddenException.class, () ->
                receiptService.updateReceipt(MOCK_ORG_ID, MOCK_RECEIPT_ID, otherUserId, request));
    }

    // ---------------------------------------------------------------------------------------------------
    // 5. 삭제 테스트 (deleteReceipt)
    // ---------------------------------------------------------------------------------------------------

    @Test
    @DisplayName("삭제 성공 - 기록자 및 멤버 권한 일치")
    void shouldDeleteReceiptSuccess() {
        // GIVEN
        when(receiptRepository.findByIdWithRelations(MOCK_RECEIPT_ID)).thenReturn(Optional.of(mockReceipt));
        // 2. 인가 Mocking: 멤버 권한 있음
        when(receiptRepository.isOrganizationOwner(MOCK_ORG_ID, MOCK_USER_ID)).thenReturn(false);
        when(memberRepository.existsById_OrgIdAndId_UserId(MOCK_ORG_ID, MOCK_USER_ID)).thenReturn(true);
        // 4. 기록자 확인 통과 (mockReceipt는 MOCK_USER_ID가 기록)

        // WHEN
        receiptService.deleteReceipt(MOCK_ORG_ID, MOCK_RECEIPT_ID, MOCK_USER_ID);

        // THEN
        // Repository.delete()가 정확히 한 번 호출되었는지 검증
        verify(receiptRepository, times(1)).delete(mockReceipt);
    }

    @Test
    @DisplayName("삭제 실패 - 조직 ID 불일치 (404)")
    void shouldThrowNotFoundWhenOrgIdMismatchesOnDelete() {
        // GIVEN
        Integer wrongOrgId = 999;
        when(receiptRepository.findByIdWithRelations(MOCK_RECEIPT_ID)).thenReturn(Optional.of(mockReceipt));
        // (mockReceipt의 Organization ID는 MOCK_ORG_ID=1)

        // WHEN & THEN
        assertThrows(NotFoundException.class, () ->
                receiptService.deleteReceipt(wrongOrgId, MOCK_RECEIPT_ID, MOCK_USER_ID));

        // delete() 메서드는 호출되지 않았는지 검증
        verify(receiptRepository, never()).delete(any());
    }
}