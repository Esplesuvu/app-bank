package com.example.appbank.web;

import com.example.appbank.domain.dto.DashboardStatsDTO;
import com.example.appbank.service.BankService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final BankService bankService;

    public DashboardController(BankService bankService) {
        this.bankService = bankService;
    }

    @GetMapping("/summary")
    public DashboardStatsDTO summary() {
        return bankService.getDashboardStats();
    }
}
