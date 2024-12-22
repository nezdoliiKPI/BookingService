package com.example.lab2.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.lab2.entity.User;
import com.example.lab2.security.AuthenticationHandler;
import com.example.lab2.security.UserDetailsImpl;
import com.example.lab2.service.user.UserService;
import com.example.lab2.validation.UserFormConstraint;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;

@CrossOrigin
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
 
    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(
        @UserFormConstraint 
        @RequestBody User userForm
    ) {
        final Optional<User> newUser = userService.createUser(userForm);

        return newUser.isPresent()
            ? new ResponseEntity<>(newUser.get(), HttpStatus.OK)
            : new ResponseEntity<>("Користувач з таким паролем або імейлом вже інує", HttpStatus.CONFLICT);
    }

    @GetMapping("/log-in")
    public ResponseEntity<?> logIn() {
        var userDetails = (UserDetailsImpl)
            SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        return userDetails != null
            ? new ResponseEntity<>(userDetails.getUser(), HttpStatus.OK)
            : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUser(
        @UserFormConstraint 
        @RequestBody User userForm
    ) {
        Authentication authentication = 
            SecurityContextHolder.getContext().getAuthentication();
        
        var userDetails = ((UserDetailsImpl)authentication.getPrincipal());

        userForm.setUserId(userDetails.getId());
        
        return userService.updateUser(userForm)
            .map(updatedUser -> new ResponseEntity<>(updatedUser, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.CONFLICT));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(@RequestParam long id) {
        Authentication authentication = 
            SecurityContextHolder.getContext().getAuthentication();

        if (!AuthenticationHandler.mayUser(authentication)
                .hasUserRole()
                .isOwner(id)
                .or()
                .hasAdminRole()
                .handle()
        ) 
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        return userService.deleteUser(id)
           ? new ResponseEntity<>(HttpStatus.OK)
           : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/score")
    public ResponseEntity<?> getUserScore(
        @Nonnull @RequestParam("user-id") Long id
    ) {
        Optional<User> user = userService.getUser(id);

        return user.isPresent()
           ? new ResponseEntity<>(user.get().getOwnerScore(),HttpStatus.OK)
           : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
