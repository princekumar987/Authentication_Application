package com.prince.auth_app.controllers;

import com.prince.auth_app.Security.JwtService;
import com.prince.auth_app.dtos.UserDto;
import com.prince.auth_app.dtos.responseDtos.LoginRequest;
import com.prince.auth_app.dtos.responseDtos.TokenResponse;
import com.prince.auth_app.entities.RefreshToken;
import com.prince.auth_app.entities.User;
import com.prince.auth_app.exceptions.ResourceNotFoundException;
import com.prince.auth_app.repositories.RefreshTokenRepository;
import com.prince.auth_app.repositories.UserRepository;
import com.prince.auth_app.services.AuthService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class AuthController {

    private AuthService authService;
    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private JwtService jwtService;
    private ModelMapper modelMapper;
    private RefreshTokenRepository refreshTokenRepository;

    @PostMapping("/login")
    private ResponseEntity<TokenResponse> login(@RequestBody LoginRequest loginRequest){

            // authenticate user
            Authentication authentication=authenticate(loginRequest);
            User user= userRepository.findByEmail(loginRequest.email()).orElseThrow(()->new BadCredentialsException("Invalid username or password"));

            if(!user.isEnabled()){
                throw  new DisabledException("User is disbabled");
            }

             String jti= UUID.randomUUID().toString();
             RefreshToken refreshTokenObj = RefreshToken.builder()
                     .jti(jti)
                     .user(user)
                     .createdAt(Instant.now())
                     .expiresAt(Instant.now().plusSeconds(jwtService.getRefrshTtlSeconds()))
                     .revoked(false)
                     .build();

             refreshTokenRepository.save(refreshTokenObj);

             String accessToken=jwtService.generateAccessToken(user);
             String refreshToken= jwtService.generateRefreshToken(user,refreshTokenObj.getJti());

             TokenResponse tokenResponse =TokenResponse.of(accessToken, refreshToken, jwtService.getAccessTtlSeconds(), modelMapper.map(user,UserDto.class));

             return ResponseEntity.ok(tokenResponse);

    }

    private Authentication authenticate(LoginRequest loginRequest) {
        try {
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));
        }
        catch(Exception e){
             throw  new BadCredentialsException("Invalid userName or Password");
        }

    }

    @PostMapping("/register")
    private ResponseEntity<UserDto> registerUser(@RequestBody UserDto userDto){
           return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerUser(userDto));
    }
}
