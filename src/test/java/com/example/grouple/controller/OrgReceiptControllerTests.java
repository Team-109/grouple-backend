package com.example.grouple.controller;

import com.example.grouple.api.ApiResponse;
import com.example.grouple.dto.receipt.request.ReceiptCreateRequest;
import com.example.grouple.dto.receipt.request.ReceiptUpdateRequest;
import com.example.grouple.dto.receipt.response.*;
import com.example.grouple.security.AuthPrincipal;
import com.example.grouple.service.ReceiptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrgReceiptControllerTests {

    @Mock
    private ReceiptService receiptService;

    private OrgReceiptController controller;

    @Captor
    private ArgumentCaptor<PageRequest> pageRequestCaptor;

    private final AuthPrincipal principal = new AuthPrincipal(20, "user");

    @BeforeEach
    void setUp() {
        controller = new OrgReceiptController(receiptService);
    }

    @Test
    void getReceiptList_shouldReturnPagedList() {
        ReceiptSummaryResponse summary = new ReceiptSummaryResponse(1, "CARD", 1000, "FOOD", LocalDate.now(), null);
        Page<ReceiptSummaryResponse> page = new PageImpl<>(List.of(summary));
        when(receiptService.getReceiptList(eq(5), any(PageRequest.class)))
                .thenReturn(new ReceiptListResponse(page));

        ResponseEntity<?> result = controller.getReceiptList(5, PageRequest.of(0, 10, Sort.by("date").descending()));

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<?> body = (ApiResponse<?>) result.getBody();
        ReceiptListResponse data = (ReceiptListResponse) body.getData();
        assertThat(data.getData()).containsExactly(summary);
        verify(receiptService).getReceiptList(eq(5), pageRequestCaptor.capture());
        assertThat(pageRequestCaptor.getValue().getSort().getOrderFor("date")).isNotNull();
    }

    @Test
    void createReceipt_shouldReturnCreated() {
        ReceiptCreateRequest request = new ReceiptCreateRequest("CARD", 1000, "FOOD", LocalDate.now(), null, "memo");
        ReceiptCreateResponse response = new ReceiptCreateResponse(9, "created");
        when(receiptService.createReceipt(3, principal.getId(), request)).thenReturn(response);

        ResponseEntity<?> result = controller.createReceipt(3, principal, request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        ApiResponse<?> body = (ApiResponse<?>) result.getBody();
        assertThat(body.getData()).isEqualTo(response);
        verify(receiptService).createReceipt(3, principal.getId(), request);
    }

    @Test
    void getReceiptDetails_shouldReturnDetail() {
        ReceiptDetailResponse response = new ReceiptDetailResponse(1, "CARD", 1000, "FOOD", "desc",
                LocalDate.now(), null, principal.getId(), principal.getUsername());
        when(receiptService.viewReceipt(4, 11, principal.getId())).thenReturn(response);

        ResponseEntity<?> result = controller.getReceiptDetails(4, 11, principal);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<?> body = (ApiResponse<?>) result.getBody();
        assertThat(body.getData()).isEqualTo(response);
        verify(receiptService).viewReceipt(4, 11, principal.getId());
    }

    @Test
    void updateReceipt_shouldReturnUpdated() {
        ReceiptUpdateRequest request = new ReceiptUpdateRequest("CASH", 2000, "ETC", "updated", LocalDate.now(), null);
        ReceiptUpdateResponse response = new ReceiptUpdateResponse(7, "updated");
        when(receiptService.updateReceipt(2, 7, principal.getId(), request)).thenReturn(response);

        ResponseEntity<?> result = controller.updateReceipt(2, 7, principal, request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<?> body = (ApiResponse<?>) result.getBody();
        assertThat(body.getData()).isEqualTo(response);
        verify(receiptService).updateReceipt(2, 7, principal.getId(), request);
    }

    @Test
    void deleteReceipt_shouldReturnNoContent() {
        ResponseEntity<?> result = controller.deleteReceipt(9, 33, principal);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(receiptService).deleteReceipt(9, 33, principal.getId());
    }
}
