package com.example.grouple.controller;

import com.example.grouple.api.ApiResponse;
import com.example.grouple.dto.receipt.request.ReceiptCreateRequest;
import com.example.grouple.dto.receipt.request.ReceiptUpdateRequest;
import com.example.grouple.dto.receipt.response.*;
import com.example.grouple.security.AuthPrincipal;
import com.example.grouple.service.ReceiptService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "08. 조직 가계부")
@RestController
@RequestMapping("/organizations/{orgId}/receipts")
public class OrgReceiptController extends BaseController {

    private final ReceiptService receiptService;

    public OrgReceiptController(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getReceiptList(
            @PathVariable Integer orgId,
            @Parameter(description = "페이징 및 정렬 정보",
                    example = "{\"page\": 0, \"size\": 10, \"sort\": \"date,DESC\"}")
            @PageableDefault(sort = "date", direction = Sort.Direction.DESC)
            Pageable pageable) {
        ReceiptListResponse response = receiptService.getReceiptList(orgId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createReceipt(
            @PathVariable Integer orgId,
            @AuthenticationPrincipal AuthPrincipal principal,
            @RequestBody @Valid ReceiptCreateRequest request) {
        Integer currentUserId = requireUserId(principal);
        ReceiptCreateResponse response = receiptService.createReceipt(orgId, currentUserId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @GetMapping("/{receiptId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getReceiptDetails(
            @PathVariable Integer orgId,
            @PathVariable Integer receiptId,
            @AuthenticationPrincipal AuthPrincipal principal
    ) {
        Integer currentUserId = requireUserId(principal);
        ReceiptDetailResponse response = receiptService.viewReceipt(orgId, receiptId, currentUserId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{receiptId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateReceipt(
            @PathVariable Integer orgId,
            @PathVariable Integer receiptId,
            @AuthenticationPrincipal AuthPrincipal principal,
            @RequestBody @Valid ReceiptUpdateRequest request
    ) {
        Integer currentUserId = requireUserId(principal);
        ReceiptUpdateResponse response = receiptService.updateReceipt(orgId, receiptId, currentUserId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{receiptId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteReceipt(
            @PathVariable Integer orgId,
            @PathVariable Integer receiptId,
            @AuthenticationPrincipal AuthPrincipal principal
    ) {
        Integer currentUserId = requireUserId(principal);
        receiptService.deleteReceipt(orgId, receiptId, currentUserId);
        return ResponseEntity.noContent().build();
    }
}
