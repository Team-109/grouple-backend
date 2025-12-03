package com.example.grouple.dto.receipt.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ReceiptUpdateRequest(
        @NotBlank(message = "유형은 필수입니다.")
        String type, // (예: 수입/지출)

        @NotNull(message = "금액은 필수입니다.")
        Integer amount,

        @NotBlank(message = "카테고리는 필수입니다.")
        String category,

        String description,

        @NotNull(message = "날짜는 필수입니다.")
        LocalDate date,

        String image // 이미지 경로는 선택 사항
) {
}