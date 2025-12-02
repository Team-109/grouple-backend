package com.example.grouple.dto.receipt.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor

//조회에 필요한 필드
public class ReceiptSummaryResponse {
    private final Integer id;
    private final String type; // 지출 유형
    private final Integer amount; // 금액
    private final String category; // 카테고리
    private final LocalDate date; // 날짜
    private final String image; // 이미지 URL
}
