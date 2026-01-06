package com.example.appbank.repository;

import com.example.appbank.domain.entities.AccountOperation;
import com.example.appbank.domain.enums.OperationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AccountOperationRepository extends JpaRepository<AccountOperation, Long> {
    Page<AccountOperation> findByBankAccount_Id(String accountId, Pageable pageable);

    @Query("select coalesce(sum(op.amount), 0) from AccountOperation op where op.type = :type")
    double sumByType(OperationType type);
}
