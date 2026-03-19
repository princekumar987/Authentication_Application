package com.prince.auth_app.Security;

import com.prince.auth_app.entities.User;
import com.prince.auth_app.exceptions.ResourceNotFoundException;
import com.prince.auth_app.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user=userRepository.findByEmail(username).orElseThrow(()->new ResourceNotFoundException("Invalid Email or Password"));
        return user;
    }
}
