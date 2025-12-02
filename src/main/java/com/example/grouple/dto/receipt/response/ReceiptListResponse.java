package com.example.grouple.dto.receipt.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import java.util.List;

@Getter
@NoArgsConstructor

//페이징 메타 정보와 ReceiptSummaryResponse 리스트를 포함하는 최종 응답 컨테이너
public class ReceiptListResponse {
    private List<ReceiptSummaryResponse> data;

    // 페이징 메타 정보
    private long totalCount;
    private int totalPages;
    private int currentPage;
    private boolean isFirst;
    private boolean isLast;

    // Page<DTO>를 받아 ListResponse를 만드는 생성자
    public ReceiptListResponse(Page<ReceiptSummaryResponse> page) {
        this.data = page.getContent();
        this.totalCount = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.currentPage = page.getNumber();
        this.isFirst = page.isFirst();
        this.isLast = page.isLast();
    }
}