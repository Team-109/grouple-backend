package com.example.grouple.service;

import com.example.grouple.dto.receipt.request.ReceiptCreateRequest;
import com.example.grouple.dto.receipt.request.ReceiptUpdateRequest;
import com.example.grouple.dto.receipt.response.*;
import com.example.grouple.entity.Organization;
import com.example.grouple.entity.Receipt;
import com.example.grouple.entity.User;
import com.example.grouple.repository.MemberRepository;
import com.example.grouple.repository.OrganizationRepository;
import com.example.grouple.repository.ReceiptRepository;
import com.example.grouple.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ReceiptService {
    private final MemberRepository memberRepository;
    private final ReceiptRepository receiptRepository;
    private final OrganizationRepository organizationRepository; // 가정
    private final UserRepository userRepository;

    public ReceiptListResponse getReceiptList(Integer organizationId, Pageable pageable) {

        // 1. Repository에서 부분 조회된 Page<DTO>를 받음
        Page<ReceiptSummaryResponse> receiptPage = receiptRepository.findSummariesByOrganizationId(
               organizationId, pageable);

        // 2. 최종 응답 DTO의 생성자를 호출해 Page 객체 변환
        return new ReceiptListResponse(receiptPage);
    }

    @Transactional
    public ReceiptCreateResponse createReceipt(
            Integer organizationId,
            Integer userId, // 로그인 사용자 ID는 Security Context에서 가져온다고 가정
            ReceiptCreateRequest request) {

        //조직 및 사용자 엔티티 조회 (유효성 검증 및 연관 관계 설정을 위해 필수)
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "조직을 찾을 수 없습니다: " + organizationId));

        // 2. 사용자 엔티티 조회 및 404 처리
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "사용자를 찾을 수 없습니다: " + userId));

        // DTO -> Entity 변환
        //    (Receipt 엔티티에 Builder나 정적 팩토리 메서드가 있다고 가정)
        Receipt newReceipt = Receipt.builder()
                .type(request.getType())
                .amount(request.getAmount())
                .category(request.getCategory())
                .date(request.getDate())
                .image(request.getImage())
                .description(request.getDescription())
                .organization(organization) // 연관 관계 설정
                .user(user)                 // 연관 관계 설정
                .build();

        // Repository를 통해 DB에 저장 (INSERT 쿼리 발생)
        Receipt savedReceipt = receiptRepository.save(newReceipt);

        //  응답 DTO 생성 및 반환
        return new ReceiptCreateResponse(savedReceipt.getId(), "가계부 항목이 성공적으로 생성되었습니다.");
    }

    @Transactional(readOnly = true)
    public ReceiptDetailResponse viewReceipt(
            Integer orgId,
            Integer receiptId,
            Integer currentUserId // Security Context에서 가져온 ID
    ) {
        // 1. Receipt 조회 (User와 Organization 관계까지 Fetch Join)
        Receipt receipt = receiptRepository.findByIdWithRelations(receiptId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, //  404 상태 지정
                        "가계부 항목을 찾을 수 없습니다: " + receiptId));

        // 2. 유효성 검증: URL의 orgId와 실제 Receipt의 조직 ID 일치 확인
        if (!receipt.getOrganization().getId().equals(orgId)) {
            // 404로 응답하여 자원이 없는 것처럼 숨김 (보안상 권장)
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "해당 조직에 속하지 않는 항목입니다.");
        }

        // 3. 인가 검증: 현재 사용자가 해당 조직의 멤버인지 확인
        boolean isOwner = receiptRepository.isOrganizationOwner(orgId, currentUserId);
        boolean isMember = memberRepository.existsById_OrgIdAndId_UserId(orgId, currentUserId);
        if (!isMember && !isOwner) {
            throw new AccessDeniedException("이 항목을 조회할 권한이 없습니다.");
        }

        // 4. DTO 변환 및 반환
        return ReceiptDetailResponse.from(receipt);
    }

    @Transactional
    public ReceiptUpdateResponse updateReceipt(
            Integer orgId,
            Integer receiptId,
            Integer currentUserId,
            ReceiptUpdateRequest request
    ) {
        // 1. Receipt 조회 및 유효성 검증
        Receipt receipt = receiptRepository.findByIdWithRelations(receiptId) // Join Fetch 사용 가정
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "수정 대상 가계부 항목을 찾을 수 없습니다."));

        // 2. 조직 소속 유효성 검증 (URL의 orgId와 실제 Receipt의 조직 ID 일치 확인)
        if (!receipt.getOrganization().getId().equals(orgId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "해당 조직에 속하지 않는 항목입니다.");
        }

        // 3. ️ 1차 인가: 조직 멤버십 확인
        boolean isOwner = receiptRepository.isOrganizationOwner(orgId, currentUserId);
        boolean isMember = memberRepository.existsById_OrgIdAndId_UserId(orgId, currentUserId);
        if (!isMember && !isOwner) {
            throw new AccessDeniedException("이 항목을 수정할 권한이 없습니다.");
        }

        // 4. 2차 인가: 수정 권한 확인 (항목 기록자만 수정 가능)
        // OrganizationManager는 따로 role 검증이 필요하지만, 여기서는 단순 기록자 권한만 검증
        if (!receipt.getUser().getId().equals(currentUserId)) {
            throw new AccessDeniedException("본인이 기록한 항목만 수정할 수 있습니다.");
        }

        // 5. 엔티티 수정 (Dirty Checking 활용)
        // Lombok의 @Setter 대신 엔티티 내부에 update() 메서드를 구현하는 것이 권장되지만, 여기서는 Setter 사용 가정
        receipt.setType(request.type());
        receipt.setAmount(request.amount());
        receipt.setCategory(request.category());
        receipt.setDescription(request.description());
        receipt.setDate(request.date());
        receipt.setImage(request.image());

        // @Transactional에 의해 트랜잭션 종료 시 자동 반영 (Dirty Checking)

        return new ReceiptUpdateResponse(receipt.getId(), "가계부 항목이 성공적으로 수정되었습니다.");
    }

    @Transactional
    public void deleteReceipt(
            Integer orgId,
            Integer receiptId,
            Integer currentUserId
    ) {
        // 1. Receipt 조회 및 유효성 검증
        Receipt receipt = receiptRepository.findByIdWithRelations(receiptId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "삭제 대상 가계부 항목을 찾을 수 없습니다."));

        // 2. 조직 소속 유효성 검증 (URL의 orgId와 실제 Receipt의 조직 ID 일치 확인)
        if (!receipt.getOrganization().getId().equals(orgId)) {
            // 404로 응답하여 자원이 없는 것처럼 숨김
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "해당 조직에 속하지 않는 항목입니다.");
        }

        // 3. 1차 인가: 조직 멤버십 확인
        boolean isOwner = receiptRepository.isOrganizationOwner(orgId, currentUserId);
        boolean isMember = memberRepository.existsById_OrgIdAndId_UserId(orgId, currentUserId);
        if (!isMember && !isOwner) {
            throw new AccessDeniedException("이 항목을 삭제할 권한이 없습니다.");
        }

        // 4. 2차 인가: 삭제 권한 확인 (항목 기록자만 삭제 가능)
        if (!receipt.getUser().getId().equals(currentUserId)) {
            // 403 Forbidden
            throw new AccessDeniedException("본인이 기록한 항목만 삭제할 수 있습니다.");
        }

        // 5. 항목 삭제
        receiptRepository.delete(receipt);
    }
}
