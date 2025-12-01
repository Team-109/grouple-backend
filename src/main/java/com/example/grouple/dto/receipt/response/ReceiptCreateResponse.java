package com.example.grouple.dto.receipt.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReceiptCreateResponse {
    private final Integer receiptId;
    private final String message;
}
