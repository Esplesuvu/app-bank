package com.example.appbank.service;

import com.example.appbank.domain.dto.*;

import java.util.List;

public interface BankService {
    CustomerDTO saveCustomer(CustomerDTO customerDTO);

    CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO);

    void deleteCustomer(Long customerId);

    List<CustomerDTO> listCustomers();

    List<CustomerDTO> searchCustomers(String keyword);

    BankAccountDTO getBankAccount(String accountId);

    List<BankAccountDTO> bankAccountList();

    CurrentBankAccountDTO createCurrentAccount(double initialBalance, double overdraft, Long customerId);

    SavingBankAccountDTO createSavingAccount(double initialBalance, double interestRate, Long customerId);

    void debit(String accountId, double amount, String description, String performedBy);

    void credit(String accountId, double amount, String description, String performedBy);

    void transfer(String sourceAccountId, String destinationAccountId, double amount, String performedBy);

    List<AccountOperationDTO> accountHistory(String accountId);

    DashboardStatsDTO getDashboardStats();
}
