package com.example.grouple.dto.receipt.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter //  JSON 역직렬화를 위해
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptCreateRequest {

    @NotBlank(message = "유형(type)은 필수 입력 항목입니다.")
    private String type;         // 지출 유형 (카드/현금)

    @NotNull(message = "금액(amount)은 필수 입력 항목입니다.")
    private Integer amount;      // 금액

    @NotBlank(message = "카테고리(category)는 필수 입력 항목입니다.")
    private String category;     // 카테고리

    @NotNull(message = "거래 날짜(date)는 필수 입력 항목입니다.")
    private LocalDate date;      // 거래 날짜
    private String image;        // 이미지 URL
    private String description;  // 상세 설명
}