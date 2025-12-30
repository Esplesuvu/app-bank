package com.example.appbank.service;

import com.example.appbank.domain.dto.AuthRequest;
import com.example.appbank.domain.dto.AuthResponse;
import com.example.appbank.domain.dto.ChangePasswordRequest;
import com.example.appbank.domain.dto.RegisterRequest;
import com.example.appbank.domain.dto.UserAccountDTO;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(AuthRequest request);
    void changePassword(String username, ChangePasswordRequest request);
    UserAccountDTO getProfile(String username);
}
