package com.example.lab2.service.user;

import java.util.Optional;
//import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.example.lab2.entity.User;
import com.example.lab2.entity.User.UserRole;
import com.example.lab2.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserCreator {
    private final UserRepository userRepository;

    public Optional<User> createUser(User userForm) {
        //check email
        // if (!emailIsValid(userForm.getEmail()))
        //     return  Optional.ofNullable(null);
        if (userRepository.existsByEmail(userForm.getEmail()))
            return  Optional.ofNullable(null);

        //check password
        // if (!passwordIsValid(userForm.getPassword())) 
        //     return  Optional.ofNullable(null); 
        if (userRepository.existsByPassword(userForm.getPassword())) 
            return  Optional.ofNullable(null);

        //create ADMIN
        if (userForm.getUserRole() == UserRole.ADMIN) 
            return Optional.ofNullable(userRepository.save(newUser(userForm)));
        ///////////////
        
        if (userForm.getCardCode() == null) 
            return  Optional.ofNullable(null); 

        return Optional.ofNullable(userRepository.save(newUser(userForm)));
    }
  
    public User newUser(User userForm) {
        return User.builder()
            .email(userForm.getEmail())
            .password(userForm.getPassword())
            .name(userForm.getName())
            .surname(userForm.getSurname())
            .cardCode(userForm.getCardCode())
            .userRole(userForm.getUserRole())
            .build();
    }
}
