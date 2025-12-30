package com.example.appbank.domain.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateCurrentAccountRequest(
        @PositiveOrZero double initialBalance,
        @PositiveOrZero double overdraft,
        @NotNull Long customerId
) {
}
