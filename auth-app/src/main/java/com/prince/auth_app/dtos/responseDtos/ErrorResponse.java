package com.prince.auth_app.dtos.responseDtos;

import org.springframework.http.HttpStatus;

public record ErrorResponse(
        String message,
        HttpStatus status
) {
}
