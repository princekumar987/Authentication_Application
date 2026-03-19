package com.prince.auth_app.exceptions;

import com.prince.auth_app.dtos.responseDtos.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandlers {

     @ExceptionHandler(ResourceNotFoundException.class)
     public ResponseEntity<ErrorResponse> resourceNotFoundxception(ResourceNotFoundException ex){
          ErrorResponse errorResponse=new ErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
          return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
     }

     @ExceptionHandler(IllegalArgumentException.class)
     public ResponseEntity<ErrorResponse> handleIllegualArgumentException(IllegalArgumentException ex){
          ErrorResponse errorResponse=new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
          return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
     }


}
