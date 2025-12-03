package com.example.grouple.dto.receipt.response;

import com.example.grouple.entity.Receipt;
import java.time.LocalDate;

public record ReceiptDetailResponse(
        Integer id,
        String type,
        Integer amount,
        String category,
        String description,
        LocalDate date,
        String image,
        Integer recordedByUserId,
        String recordedByUsername
) {
    public static ReceiptDetailResponse from(Receipt receipt) {
        return new ReceiptDetailResponse(
                receipt.getId(),
                receipt.getType(),
                receipt.getAmount(),
                receipt.getCategory(),
                receipt.getDescription(),
                receipt.getDate(),
                receipt.getImage(),
                receipt.getUser().getId(),
                receipt.getUser().getUsername()
        );
    }
}