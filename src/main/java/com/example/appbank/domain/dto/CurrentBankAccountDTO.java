package com.example.appbank.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CurrentBankAccountDTO extends BankAccountDTO {
    private double overdraft;
}
