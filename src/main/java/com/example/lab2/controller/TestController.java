package com.example.lab2.controller;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.lab2.controller.specifications.PostFilter;
import com.example.lab2.entity.User;
import com.example.lab2.entity.booking.Booking;
import com.example.lab2.entity.post.Post;
import com.example.lab2.service.booking.BookingService;
import com.example.lab2.service.post.PostService;
import com.example.lab2.service.user.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {
    private final BookingService bookingService;
    private final PostService postService;
    private final UserService userService;

    @GetMapping("/bookings")
    public ResponseEntity<List<Booking>> getAllBookings() {
        final List<Booking> bookings = bookingService.findAll();

        return !bookings.isEmpty()
           ? new ResponseEntity<>(bookings, HttpStatus.OK)
           : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        final List<User> users = userService.findAll();

        return !users.isEmpty()
           ? new ResponseEntity<>(users, HttpStatus.OK)
           : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/user")
    public ResponseEntity<User> getUser(@RequestParam long id) {
        final Optional<User> user = userService.getUser(id);
        
        return user.isPresent()
            ? new ResponseEntity<>(user.get(), HttpStatus.OK)
            : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/posts/filter")
    public ResponseEntity<List<Post>> getAllPosts(@RequestBody Optional<PostFilter> filter) {      
        
        final List<Post> posts = filter.isPresent()
            ?  postService.findAll(filter.get())
            :  postService.findAll(new PostFilter());     

        return !posts.isEmpty()
           ? new ResponseEntity<>(posts, HttpStatus.OK)
           : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/posts")
    public ResponseEntity<List<Post>> getAllPosts() {      
        
        final List<Post> posts = postService.findAll();  

        return !posts.isEmpty()
           ? new ResponseEntity<>(posts, HttpStatus.OK)
           : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/role")
    public ResponseEntity<String> debugRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
        }

        String username = authentication.getName();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        return ResponseEntity.ok("Username: " + username + ", Roles: " + authorities.toString());
    }
}
