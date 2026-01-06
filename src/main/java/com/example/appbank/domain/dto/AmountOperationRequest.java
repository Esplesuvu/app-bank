package com.example.appbank.domain.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record AmountOperationRequest(
        @Positive double amount,
        @Size(max = 255) String description,
        String performedBy
) {
}
