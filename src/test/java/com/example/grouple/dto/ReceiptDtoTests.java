package com.example.grouple.dto;

import com.example.grouple.dto.receipt.request.ReceiptCreateRequest;
import com.example.grouple.dto.receipt.request.ReceiptUpdateRequest;
import com.example.grouple.dto.receipt.response.ReceiptCreateResponse;
import com.example.grouple.dto.receipt.response.ReceiptDetailResponse;
import com.example.grouple.dto.receipt.response.ReceiptListResponse;
import com.example.grouple.dto.receipt.response.ReceiptSummaryResponse;
import com.example.grouple.dto.receipt.response.ReceiptUpdateResponse;
import com.example.grouple.entity.Organization;
import com.example.grouple.entity.Receipt;
import com.example.grouple.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ReceiptDtoTests {

    @Test
    void receiptCreateRequest_shouldHoldValues() {
        ReceiptCreateRequest req = new ReceiptCreateRequest("CARD", 1000, "FOOD", LocalDate.of(2025, 1, 1), "img", "memo");
        assertThat(req.getType()).isEqualTo("CARD");
        assertThat(req.getAmount()).isEqualTo(1000);
    }

    @Test
    void receiptUpdateRequest_shouldHoldValues() {
        ReceiptUpdateRequest req = new ReceiptUpdateRequest("CASH", 2000, "ETC", "desc", LocalDate.of(2025, 2, 2), "img");
        assertThat(req.category()).isEqualTo("ETC");
    }

    @Test
    void receiptCreateResponse_shouldExposeFields() {
        ReceiptCreateResponse res = new ReceiptCreateResponse(1, "ok");
        assertThat(res.getReceiptId()).isEqualTo(1);
        assertThat(res.getMessage()).isEqualTo("ok");
    }

    @Test
    void receiptDetailResponse_shouldMapFromEntity() {
        User user = new User();
        user.setId(1);
        user.setUsername("tester");
        Organization org = new Organization();
        org.setId(2);
        Receipt receipt = Receipt.builder()
                .type("CARD")
                .amount(1000)
                .category("FOOD")
                .description("d")
                .date(LocalDate.of(2025, 1, 1))
                .image("img")
                .organization(org)
                .user(user)
                .build();
        receipt.setId(5);

        ReceiptDetailResponse dto = ReceiptDetailResponse.from(receipt);

        assertThat(dto.id()).isEqualTo(5);
        assertThat(dto.recordedByUserId()).isEqualTo(1);
        assertThat(dto.recordedByUsername()).isEqualTo("tester");
    }

    @Test
    void receiptListResponse_shouldExposePaging() {
        ReceiptSummaryResponse summary = new ReceiptSummaryResponse(1, "CARD", 100, "FOOD", LocalDate.now(), null);
        Page<ReceiptSummaryResponse> page = new PageImpl<>(List.of(summary), PageRequest.of(1, 1), 3);

        ReceiptListResponse list = new ReceiptListResponse(page);

        assertThat(list.getData()).containsExactly(summary);
        assertThat(list.getTotalCount()).isEqualTo(3);
        assertThat(list.getTotalPages()).isEqualTo(3);
        assertThat(list.getCurrentPage()).isEqualTo(1);
        assertThat(list.isFirst()).isFalse();
    }

    @Test
    void receiptUpdateResponse_shouldExposeFields() {
        ReceiptUpdateResponse res = new ReceiptUpdateResponse(1, "updated");
        assertThat(res.receiptId()).isEqualTo(1);
        assertThat(res.message()).isEqualTo("updated");
    }
}
