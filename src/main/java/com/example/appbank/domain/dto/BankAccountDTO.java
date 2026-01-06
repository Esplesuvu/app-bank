package com.example.appbank.domain.dto;

import com.example.appbank.domain.enums.AccountStatus;
import lombok.Data;

import java.time.Instant;

@Data
public class BankAccountDTO {
    private String id;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
    private double balance;
    private AccountStatus status;
    private CustomerDTO customer;
}
