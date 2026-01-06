package com.example.appbank.domain.dto;

import java.util.Set;

public record UserAccountDTO(Long id, String username, Set<String> roles) {
}
