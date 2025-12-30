package com.example.appbank.domain.dto;

import com.example.appbank.domain.enums.OperationType;
import lombok.Data;

import java.time.Instant;

@Data
public class AccountOperationDTO {
    private Long id;
    private Instant operationDate;
    private double amount;
    private OperationType type;
    private String description;
    private String performedBy;
}
