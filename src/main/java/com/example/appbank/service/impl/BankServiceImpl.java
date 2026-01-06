package com.example.appbank.service.impl;

import com.example.appbank.domain.dto.*;
import com.example.appbank.domain.entities.*;
import com.example.appbank.domain.enums.AccountStatus;
import com.example.appbank.domain.enums.OperationType;
import com.example.appbank.repository.AccountOperationRepository;
import com.example.appbank.repository.BankAccountRepository;
import com.example.appbank.repository.CustomerRepository;
import com.example.appbank.service.BankService;
import com.example.appbank.service.exception.InsufficientFundsException;
import com.example.appbank.service.exception.ResourceNotFoundException;
import com.example.appbank.service.exception.TransferNotAllowedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class BankServiceImpl implements BankService {

    private final CustomerRepository customerRepository;
    private final BankAccountRepository bankAccountRepository;
    private final AccountOperationRepository accountOperationRepository;

    public BankServiceImpl(CustomerRepository customerRepository, BankAccountRepository bankAccountRepository, AccountOperationRepository accountOperationRepository) {
        this.customerRepository = customerRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.accountOperationRepository = accountOperationRepository;
    }

    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        Customer customer = new Customer();
        customer.setName(customerDTO.getName());
        customer.setEmail(customerDTO.getEmail());
        return mapCustomerToDTO(customerRepository.save(customer));
    }

    @Override
    public CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        customer.setName(customerDTO.getName());
        customer.setEmail(customerDTO.getEmail());
        return mapCustomerToDTO(customerRepository.save(customer));
    }

    @Override
    public void deleteCustomer(Long customerId) {
        customerRepository.deleteById(customerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDTO> listCustomers() {
        return customerRepository.findAll().stream().map(this::mapCustomerToDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDTO> searchCustomers(String keyword) {
        return customerRepository.findByNameContainingIgnoreCase(keyword).stream().map(this::mapCustomerToDTO).toList();
    }

    @Override
    public BankAccountDTO getBankAccount(String accountId) {
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Bank account not found"));
        return mapAccountToDTO(bankAccount);
    }

    @Override
    public List<BankAccountDTO> bankAccountList() {
        return bankAccountRepository.findAll().stream().map(this::mapAccountToDTO).toList();
    }

    @Override
    public CurrentBankAccountDTO createCurrentAccount(double initialBalance, double overdraft, Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        CurrentAccount account = new CurrentAccount();
        account.setId(UUID.randomUUID().toString());
        account.setBalance(initialBalance);
        account.setOverdraft(overdraft);
        account.setStatus(AccountStatus.CREATED);
        account.setCustomer(customer);
        bankAccountRepository.save(account);
        return (CurrentBankAccountDTO) mapAccountToDTO(account);
    }

    @Override
    public SavingBankAccountDTO createSavingAccount(double initialBalance, double interestRate, Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        SavingAccount account = new SavingAccount();
        account.setId(UUID.randomUUID().toString());
        account.setBalance(initialBalance);
        account.setInterestRate(interestRate);
        account.setStatus(AccountStatus.CREATED);
        account.setCustomer(customer);
        bankAccountRepository.save(account);
        return (SavingBankAccountDTO) mapAccountToDTO(account);
    }

    @Override
    public void debit(String accountId, double amount, String description, String performedBy) {
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Bank account not found"));
        double allowedBalance = bankAccount.getBalance();
        if (bankAccount instanceof CurrentAccount currentAccount) {
            allowedBalance += currentAccount.getOverdraft();
        }
        if (amount > allowedBalance) {
            throw new InsufficientFundsException("Insufficient funds");
        }
        AccountOperation operation = AccountOperation.builder()
                .operationDate(Instant.now())
                .amount(amount)
                .type(OperationType.DEBIT)
                .description(description)
                .performedBy(performedBy)
                .bankAccount(bankAccount)
                .build();
        accountOperationRepository.save(operation);
        bankAccount.getOperations().add(operation);
        bankAccount.setBalance(bankAccount.getBalance() - amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void credit(String accountId, double amount, String description, String performedBy) {
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Bank account not found"));
        AccountOperation operation = AccountOperation.builder()
                .operationDate(Instant.now())
                .amount(amount)
                .type(OperationType.CREDIT)
                .description(description)
                .performedBy(performedBy)
                .bankAccount(bankAccount)
                .build();
        accountOperationRepository.save(operation);
        bankAccount.getOperations().add(operation);
        bankAccount.setBalance(bankAccount.getBalance() + amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void transfer(String sourceAccountId, String destinationAccountId, double amount, String performedBy) {
        if (sourceAccountId.equals(destinationAccountId)) {
            throw new TransferNotAllowedException("Cannot transfer to the same account");
        }
        debit(sourceAccountId, amount, "Transfer to " + destinationAccountId, performedBy);
        credit(destinationAccountId, amount, "Transfer from " + sourceAccountId, performedBy);
    }

    @Override
    public List<AccountOperationDTO> accountHistory(String accountId) {
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Bank account not found"));
        return bankAccount.getOperations().stream()
                .sorted((o1, o2) -> o2.getOperationDate().compareTo(o1.getOperationDate()))
                .map(this::mapOperationToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsDTO getDashboardStats() {
        long customerCount = customerRepository.count();
        long totalAccounts = bankAccountRepository.count();
        long totalCurrentAccounts = bankAccountRepository.countCurrentAccounts();
        long totalSavingAccounts = bankAccountRepository.countSavingAccounts();
        double totalBalance = bankAccountRepository.sumBalances();
        long totalOperations = accountOperationRepository.count();
        double totalDebits = accountOperationRepository.sumByType(OperationType.DEBIT);
        double totalCredits = accountOperationRepository.sumByType(OperationType.CREDIT);

        return DashboardStatsDTO.builder()
                .totalCustomers(customerCount)
                .totalAccounts(totalAccounts)
                .totalCurrentAccounts(totalCurrentAccounts)
                .totalSavingAccounts(totalSavingAccounts)
                .totalBalance(totalBalance)
                .totalOperations(totalOperations)
                .totalDebits(totalDebits)
                .totalCredits(totalCredits)
                .build();
    }

    private CustomerDTO mapCustomerToDTO(Customer customer) {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getId());
        dto.setName(customer.getName());
        dto.setEmail(customer.getEmail());
        dto.setCreatedAt(customer.getCreatedAt());
        dto.setUpdatedAt(customer.getUpdatedAt());
        dto.setCreatedBy(customer.getCreatedBy());
        dto.setUpdatedBy(customer.getUpdatedBy());
        return dto;
    }

    private BankAccountDTO mapAccountToDTO(BankAccount account) {
        if (account instanceof SavingAccount savingAccount) {
            SavingBankAccountDTO dto = new SavingBankAccountDTO();
            mapBaseAccount(account, dto);
            dto.setInterestRate(savingAccount.getInterestRate());
            return dto;
        }
        if (account instanceof CurrentAccount currentAccount) {
            CurrentBankAccountDTO dto = new CurrentBankAccountDTO();
            mapBaseAccount(account, dto);
            dto.setOverdraft(currentAccount.getOverdraft());
            return dto;
        }
        return mapBaseAccount(account, new BankAccountDTO());
    }

    private <T extends BankAccountDTO> T mapBaseAccount(BankAccount account, T dto) {
        dto.setId(account.getId());
        dto.setCreatedAt(account.getCreatedAt());
        dto.setUpdatedAt(account.getUpdatedAt());
        dto.setCreatedBy(account.getCreatedBy());
        dto.setUpdatedBy(account.getUpdatedBy());
        dto.setBalance(account.getBalance());
        dto.setStatus(account.getStatus());
        dto.setCustomer(mapCustomerToDTO(account.getCustomer()));
        return dto;
    }

    private AccountOperationDTO mapOperationToDTO(AccountOperation operation) {
        AccountOperationDTO dto = new AccountOperationDTO();
        dto.setId(operation.getId());
        dto.setOperationDate(operation.getOperationDate());
        dto.setAmount(operation.getAmount());
        dto.setType(operation.getType());
        dto.setDescription(operation.getDescription());
        dto.setPerformedBy(operation.getPerformedBy());
        return dto;
    }
}
