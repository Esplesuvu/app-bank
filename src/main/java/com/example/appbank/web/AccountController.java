package com.example.appbank.web;

import com.example.appbank.domain.dto.*;
import com.example.appbank.service.BankService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final BankService bankService;

    public AccountController(BankService bankService) {
        this.bankService = bankService;
    }

    @GetMapping
    public List<BankAccountDTO> accounts() {
        return bankService.bankAccountList();
    }

    @GetMapping("/{id}")
    public BankAccountDTO getAccount(@PathVariable String id) {
        return bankService.getBankAccount(id);
    }

    @PostMapping("/current")
    @ResponseStatus(HttpStatus.CREATED)
    public CurrentBankAccountDTO createCurrent(@Valid @RequestBody CreateCurrentAccountRequest request) {
        return bankService.createCurrentAccount(request.initialBalance(), request.overdraft(), request.customerId());
    }

    @PostMapping("/saving")
    @ResponseStatus(HttpStatus.CREATED)
    public SavingBankAccountDTO createSaving(@Valid @RequestBody CreateSavingAccountRequest request) {
        return bankService.createSavingAccount(request.initialBalance(), request.interestRate(), request.customerId());
    }

    @PostMapping("/{id}/debit")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void debit(@PathVariable String id, @Valid @RequestBody AmountOperationRequest request) {
        bankService.debit(id, request.amount(), request.description(), performedByOrDefault(request.performedBy()));
    }

    @PostMapping("/{id}/credit")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void credit(@PathVariable String id, @Valid @RequestBody AmountOperationRequest request) {
        bankService.credit(id, request.amount(), request.description(), performedByOrDefault(request.performedBy()));
    }

    @PostMapping("/transfer")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void transfer(@Valid @RequestBody TransferRequest request) {
        bankService.transfer(request.sourceAccountId(), request.destinationAccountId(), request.amount(), performedByOrDefault(request.performedBy()));
    }

    @GetMapping("/{id}/operations")
    public List<AccountOperationDTO> operations(@PathVariable String id) {
        return bankService.accountHistory(id);
    }

    private String performedByOrDefault(String performedBy) {
        if (performedBy != null && !performedBy.isBlank()) {
            return performedBy;
        }
        var authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return "system";
    }
}
