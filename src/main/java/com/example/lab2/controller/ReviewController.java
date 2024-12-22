package com.example.lab2.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.lab2.entity.PublicationStatus;
import com.example.lab2.entity.Review;
import com.example.lab2.entity.booking.Booking;
import com.example.lab2.entity.post.Post;
import com.example.lab2.security.AuthenticationHandler;
import com.example.lab2.security.UserDetailsImpl;
import com.example.lab2.service.booking.BookingService;
import com.example.lab2.service.post.PostService;
import com.example.lab2.service.review.ReviewService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    private final BookingService bookingService;
    private final PostService postService;

    @GetMapping("")
    public ResponseEntity<Review> getReview(@RequestParam(name = "review-id") Long id) {

        return reviewService.getReview(id)
           .map(review -> new ResponseEntity<>(review, HttpStatus.OK))
           .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
 
    @PostMapping("/create")
    public ResponseEntity<Review> createReview(@Valid @RequestBody Review review) {
        Authentication authentication = 
            SecurityContextHolder.getContext().getAuthentication();

        final Long userId = ((UserDetailsImpl)authentication.getPrincipal()).getId();
        review.setAuthorId(userId);

        Optional<Booking> result = bookingService.getBooking(review.getBookingId());

        if (result.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        if (!result.get().getBookingDetails().getUserId().equals(userId))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        return reviewService.createReview(review)
           .map(newReview -> new ResponseEntity<>(newReview, HttpStatus.OK))
           .orElse(new ResponseEntity<>(HttpStatus.CONFLICT));
    }

    @GetMapping("/post")
    public ResponseEntity<List<Review>> getPostReviews(@RequestParam(name = "post-id") Long postId) {
        final Optional<Post> result = postService.findPost(postId);

        if (result.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        final Post post = result.get();
        final List<Review> reviews = reviewService.getPostAvaliableReviews(post.getId());

        Authentication authentication = 
            SecurityContextHolder.getContext().getAuthentication();

        if (post.getPostStatus() != PublicationStatus.ON_DELETE) {
            return !reviews.isEmpty()
                ? new ResponseEntity<>(reviews, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        if (authentication != null &&
            authentication.isAuthenticated() &&
            //when Anonymous Authentication is enabled
            authentication instanceof AnonymousAuthenticationToken 
        )
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        if (post.getPostStatus() == PublicationStatus.UNAVAILABLE) {
            if (AuthenticationHandler.mayUser(authentication)
                .hasUserRole()
                .isOwner(post.getLand().getOwnerId())
                .or()
                .hasAdminRole()
                .handle()
            ) {
                return !reviews.isEmpty()
                    ? new ResponseEntity<>(reviews, HttpStatus.OK)
                    : new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }

        if (post.getPostStatus() == PublicationStatus.ON_DELETE) {
            if (AuthenticationHandler.mayUser(authentication)
                .hasAdminRole()
                .handle()
            ) {
                return !reviews.isEmpty()
                    ? new ResponseEntity<>(reviews, HttpStatus.OK)
                    : new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/booking")
    public ResponseEntity<Review> getBookingReview(@RequestParam(name = "booking-id") Long bookingId) {
        Authentication authentication = 
            SecurityContextHolder.getContext().getAuthentication();

        Optional<Booking> result = bookingService.getBooking(bookingId);

        if (result.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        if (!AuthenticationHandler.mayUser(authentication)
            .hasUserRole()
            .isOwner(bookingId)
            .or()
            .hasAdminRole()
            .handle()
        )
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        return reviewService.getBookingReview(bookingId)
           .map(review -> new ResponseEntity<>(review, HttpStatus.OK))
           .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
