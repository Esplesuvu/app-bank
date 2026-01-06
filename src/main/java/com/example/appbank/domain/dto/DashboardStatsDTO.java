package com.example.appbank.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    private long totalCustomers;
    private long totalAccounts;
    private long totalCurrentAccounts;
    private long totalSavingAccounts;
    private long totalOperations;
    private double totalBalance;
    private double totalDebits;
    private double totalCredits;
}
