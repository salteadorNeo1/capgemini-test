package com.capgemini.test.code.dto;

public record UserResponse(
        Long id,
        Long roomId,
        String name,
        String email,
        String phone,
        String rol,
        String dni
) {}