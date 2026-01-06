package com.example.appbank.repository;

import com.example.appbank.domain.entities.BankAccount;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository extends JpaRepository<BankAccount, String> {
    @Query("select count(ca) from CurrentAccount ca")
    long countCurrentAccounts();

    @Query("select count(sa) from SavingAccount sa")
    long countSavingAccounts();

    @Query("select coalesce(sum(b.balance), 0) from BankAccount b")
    double sumBalances();
}
