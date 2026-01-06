package com.example.appbank.domain.entities;

import com.example.appbank.domain.enums.OperationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class AccountOperation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Instant operationDate;

    private double amount;

    @Enumerated(EnumType.STRING)
    private OperationType type;

    private String description;

    private String performedBy;

    @ManyToOne
    private BankAccount bankAccount;
}
