package com.example.appbank.config;

import com.example.appbank.domain.dto.CustomerDTO;
import com.example.appbank.domain.entities.UserAccount;
import com.example.appbank.repository.UserAccountRepository;
import com.example.appbank.service.BankService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Set;

@Configuration
public class DatabaseInitializer {

    @Bean
    CommandLineRunner loadData(BankService bankService,
                               UserAccountRepository userAccountRepository,
                               PasswordEncoder passwordEncoder) {
        return args -> {
            if (bankService.listCustomers().isEmpty()) {
                List.of(
                        new CustomerDTO(null, "Alice", "alice@example.com", null, null, null, null),
                        new CustomerDTO(null, "Bob", "bob@example.com", null, null, null, null),
                        new CustomerDTO(null, "Charlie", "charlie@example.com", null, null, null, null)
                ).forEach(bankService::saveCustomer);
            }
            var customers = bankService.listCustomers();
            if (!customers.isEmpty() && bankService.bankAccountList().isEmpty()) {
                customers.forEach(customer -> {
                    bankService.createCurrentAccount(5000, 1000, customer.getId());
                    bankService.createSavingAccount(8000, 3.5, customer.getId());
                });
            }

            if (userAccountRepository.count() == 0) {
                userAccountRepository.save(UserAccount.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin"))
                        .roles(Set.of("ROLE_ADMIN", "ROLE_USER"))
                        .build());
            }
        };
    }
}
