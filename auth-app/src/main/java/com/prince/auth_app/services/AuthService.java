package com.prince.auth_app.services;

import com.prince.auth_app.dtos.UserDto;

public interface AuthService {

    UserDto registerUser(UserDto userDto);
}
