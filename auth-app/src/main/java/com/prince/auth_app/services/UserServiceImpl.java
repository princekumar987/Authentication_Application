package com.prince.auth_app.services;

import com.prince.auth_app.dtos.UserDto;
import com.prince.auth_app.entities.Provider;
import com.prince.auth_app.entities.User;
import com.prince.auth_app.exceptions.ResourceNotFoundException;
import com.prince.auth_app.helpers.Userhelper;
import com.prince.auth_app.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private ModelMapper modelMapper;
    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {

        if(userDto.getEmail()==null || userDto.getEmail().isBlank()){
             throw  new IllegalArgumentException("Email is Required");
        }
        if(userRepository.existsByEmail(userDto.getEmail())){
             throw  new IllegalArgumentException("Email already exist");
        }
        //convert userdto to user
        User user=modelMapper.map(userDto, User.class);
        //role assign in future
        // TODO
        user.setProvider(userDto.getProvider()!=null?userDto.getProvider(): Provider.LOCAL);
        User savedUser=userRepository.save(user);

        return modelMapper.map(savedUser,UserDto.class);
    }

    @Override
    @Transactional
    public UserDto getUserByEmail(String email) {

        User user=userRepository.findByEmail(email).orElseThrow(()-> new ResourceNotFoundException("user not found"));
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    @Transactional
    public UserDto updateUser(UserDto userDto, String uId) {
        UUID userId=Userhelper.parseUuid(uId);
        User existingUser=userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("user not exist with this id"));

        // we are not going to update the email of the user
        if(userDto.getName()!=null)existingUser.setName(userDto.getName());
        if(userDto.getImage()!=null)existingUser.setImage(userDto.getImage());
        if(userDto.getProvider()!=null)existingUser.setProvider(userDto.getProvider());
        //TODO hashing password value
        if(userDto.getPassword()!=null)existingUser.setPassword(userDto.getPassword());
        existingUser.setEnable(userDto.isEnable());
        existingUser.setUpdatedAt(Instant.now());

        User savedUser=userRepository.save(existingUser);

        return modelMapper.map(savedUser,UserDto.class);
    }

    @Override
    @Transactional
    public void deleteUser(String uId) {
        UUID userId= Userhelper.parseUuid(uId);
        User user=userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("User not found by this ID"));
        userRepository.delete(user);
    }

    @Override
    @Transactional
    public UserDto getUserById(String userId) {
        User user=userRepository.findById(Userhelper.parseUuid(userId)).orElseThrow(()->new ResourceNotFoundException("User not found by this ID"));
        return modelMapper.map(user,UserDto.class);
    }

    @Override
    @Transactional
    public List<UserDto> getAllUsers() {
        return userRepository
                  .findAll()
                  .stream()
                  .map(user -> modelMapper.map(user,UserDto.class))
                  .toList();
    }
}
