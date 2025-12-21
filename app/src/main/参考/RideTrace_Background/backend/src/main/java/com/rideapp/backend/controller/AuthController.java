package com.rideapp.backend.controller;

import com.rideapp.backend.dto.*;
import com.rideapp.backend.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * POST /api/v1/auth/login
     * Body: { "username": "...", "password": "..." }
     */
    @PostMapping("/login")
    public ApiResponse<AuthResultDTO> login(@RequestBody LoginRequestDTO req) {
        AuthResultDTO result = authService.login(req);
        if (result == null) {
            // 用户名不存在或密码错误
            return ApiResponse.error(1002, "用户名或密码错误");
        }
        return ApiResponse.success(result);
    }

    /**
     * POST /api/v1/auth/register
     * Body: { "username": "...", "password": "..." }
     */
    @PostMapping("/register")
    public ApiResponse<AuthResultDTO> register(@RequestBody RegisterRequestDTO req) {
        AuthResultDTO result = authService.register(req);
        if (result == null) {
            // 用户名已存在
            return ApiResponse.error(1003, "用户名已存在");
        }
        return ApiResponse.success(result);
    }
}
