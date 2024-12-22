package com.example.lab2.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.lab2.entity.Report;
import com.example.lab2.entity.Review;
import com.example.lab2.entity.post.Post;
import com.example.lab2.security.UserDetailsImpl;
import com.example.lab2.service.administration.AdminService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/posts")
    public ResponseEntity<?> getPosts(
        @RequestParam(name = "page-number") int number,
        @RequestParam(name = "page-size") int size
    ) {
        final List<Post> posts = adminService.getPosts(number, size);

        return !posts.isEmpty()
           ? new ResponseEntity<>(posts, HttpStatus.OK)
           : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/reviews")
    public ResponseEntity<?> getReviews(
        @RequestParam(name = "page-number") int number,
        @RequestParam(name = "page-size") int size
    ) {
        final List<Review> reviews = adminService.getReviews(number, size);

        return !reviews.isEmpty()
           ? new ResponseEntity<>(reviews, HttpStatus.OK)
           : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/accept-post")
    public ResponseEntity<?> acceptPost(
        @RequestParam(name = "post-id") Long postId
        ) {

        return adminService.acceptPost(postId)
            ? new ResponseEntity<>(HttpStatus.OK)
            : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/accept-review")
    public ResponseEntity<?> acceptReview(
        @RequestParam(name = "review-id") Long reviewId
        ) {

        return adminService.acceptReview(reviewId)
            ? new ResponseEntity<>(HttpStatus.OK)
            : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/deny-post")
    public ResponseEntity<?> denyPost(@Valid @RequestBody Report report) {

        Authentication authentication = 
            SecurityContextHolder.getContext().getAuthentication();
        final Long userId = ((UserDetailsImpl)authentication.getPrincipal()).getId();

        report.setAuthorId(userId);

        return adminService.denyPost(report).isPresent()
            ? new ResponseEntity<>(HttpStatus.OK)
            : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/deny-review")
    public ResponseEntity<?> denyReview(
        @NotNull @RequestParam("review-id") Long reviewId
    ) {

        return adminService.denyReview(reviewId)
            ? new ResponseEntity<>(HttpStatus.OK)
            : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
