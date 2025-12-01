package com.example.grouple.controller;

import com.example.grouple.dto.receipt.request.ReceiptCreateRequest;
import com.example.grouple.dto.receipt.request.ReceiptUpdateRequest;
import com.example.grouple.dto.receipt.response.*;
import com.example.grouple.security.AuthPrincipal;
import com.example.grouple.service.ReceiptService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "08. 조직 가계부")
@RestController
@RequiredArgsConstructor
@RequestMapping("/organizations/{orgId}/receipts")
public class OrgReceiptController {
    private final ReceiptService receiptService;

    @GetMapping
    public ResponseEntity<ReceiptListResponse> getReceiptList(
            @PathVariable Integer orgId,
            @Parameter(description = "페이징 및 정렬 정보",
                    example = "{\"page\": 0, \"size\": 10, \"sort\": \"date,DESC\"}")
            @PageableDefault(sort = "date", direction = Sort.Direction.DESC, size = 10)
            Pageable pageable) {
        ReceiptListResponse response = receiptService.getReceiptList(orgId, pageable);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ReceiptCreateResponse> createReceipt(
            @PathVariable Integer orgId,
            @AuthenticationPrincipal AuthPrincipal userDetails,
            @RequestBody @Valid ReceiptCreateRequest request) {

        // 사용자 ID는 인증/인가 과정을 통해 Security Context에서 가져와야 함
        Integer currentUserId = userDetails.getId();

        // Service 호출
        ReceiptCreateResponse response = receiptService.createReceipt(orgId, currentUserId, request);

        // HTTP 상태 코드 201 (Created)와 함께 응답 DTO를 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{receiptId}")
    public ResponseEntity<ReceiptDetailResponse> getReceiptDetails(
            @PathVariable Integer orgId,
            @PathVariable Integer receiptId,
            @AuthenticationPrincipal AuthPrincipal userDetails
    ) {
        // 1. 사용자 ID 추출 (CustomUserDetails에 getUserId()가 구현되어 있다고 가정)
        Integer currentUserId = userDetails.getId();

        // 2. Service 호출 (조직 ID, 항목 ID, 현재 사용자 ID 전달)
        ReceiptDetailResponse response = receiptService.viewReceipt(
                orgId,
                receiptId,
                currentUserId
        );

        // 3. 응답 반환
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{receiptId}")
    public ResponseEntity<ReceiptUpdateResponse> updateReceipt(
            @PathVariable Integer orgId,
            @PathVariable Integer receiptId,
            @AuthenticationPrincipal AuthPrincipal userDetails,
            @RequestBody ReceiptUpdateRequest request
    ) {
        // 1. 사용자 ID 추출
        Integer currentUserId = userDetails.getId();

        // 2. Service 호출
        ReceiptUpdateResponse response = receiptService.updateReceipt(
                orgId,
                receiptId,
                currentUserId,
                request
        );

        // 3. HTTP 200 OK와 함께 응답 반환
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{receiptId}")
    public ResponseEntity<Void> deleteReceipt(
            @PathVariable Integer orgId,
            @PathVariable Integer receiptId,
            @AuthenticationPrincipal AuthPrincipal userDetails
    ) {
        // 1. 사용자 ID 추출
        Integer currentUserId = userDetails.getId();

        // 2. Service 호출 (삭제 로직 실행)
        receiptService.deleteReceipt(
                orgId,
                receiptId,
                currentUserId
        );

        // 3. HTTP 204 No Content 반환 (RESTful API 관례)
        return ResponseEntity.noContent().build();
    }
}
