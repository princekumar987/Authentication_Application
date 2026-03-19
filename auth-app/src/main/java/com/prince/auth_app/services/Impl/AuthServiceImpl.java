package com.prince.auth_app.services.Impl;

import com.prince.auth_app.dtos.UserDto;
import com.prince.auth_app.services.AuthService;
import com.prince.auth_app.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private UserService userService;
    @Override
    public UserDto registerUser(UserDto userDto) {

        //verify email
        //verify password
        //default roles
        UserDto user=userService.createUser(userDto);
        return user;
    }
}
