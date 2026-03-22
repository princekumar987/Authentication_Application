package com.prince.auth_app.dtos.responseDtos;

public record LoginRequest(
        String email,
        String password
) {
}
