package com.prince.auth_app.controllers;

import com.prince.auth_app.dtos.UserDto;
import com.prince.auth_app.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {

    private UserService userService;

    //create user API
    @PostMapping
    private ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto){
         return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userDto));
    }

    //get Allusers API
    @GetMapping
    private ResponseEntity<List<UserDto>> getAllUsers(){
         return ResponseEntity.ok(userService.getAllUsers());
    }

    //get user by email
    @GetMapping("/email")
    private ResponseEntity<UserDto> getUserByEmail(@RequestParam (name="email") String email){
          return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    //get user by id
    @GetMapping("/{userId}")
    private ResponseEntity<UserDto> getUserById(@PathVariable String userId){
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    //delete User By Id
    @DeleteMapping("/{userId}")
    private ResponseEntity<String> deleteUserById(@PathVariable String userId){
          userService.deleteUser(userId);
         return ResponseEntity.ok().body("user deleted");
    }

    //Update User By Id
    @PatchMapping("/{userId}")
    private ResponseEntity<UserDto> updateUserById(@PathVariable String userId, @RequestBody UserDto userDto){
        return ResponseEntity.ok().body(userService.updateUser(userDto,userId));
    }
}
