package com.example.appbank.domain.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateSavingAccountRequest(
        @PositiveOrZero double initialBalance,
        @PositiveOrZero double interestRate,
        @NotNull Long customerId
) {
}
