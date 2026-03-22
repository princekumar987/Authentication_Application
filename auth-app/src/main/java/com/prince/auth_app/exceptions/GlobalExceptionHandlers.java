package com.prince.auth_app.exceptions;

import com.prince.auth_app.dtos.responseDtos.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandlers {

     private static final Logger logger= LoggerFactory.getLogger(GlobalExceptionHandlers.class);

     @ExceptionHandler({
                     UsernameNotFoundException.class,
                     BadCredentialsException.class,
                     CredentialsExpiredException.class,
                     DisabledException.class
     })
     public ResponseEntity<ErrorResponse> handleAuthException(Exception e, HttpServletRequest request){
          logger.info(" Exception {} ", e.getClass().getName());
          ErrorResponse errorResponse=new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
          return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
     }
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
