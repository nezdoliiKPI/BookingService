package com.example.lab2.service.review;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.lab2.entity.PublicationStatus;
import com.example.lab2.entity.Review;
import com.example.lab2.entity.User;
import com.example.lab2.entity.booking.Booking;
import com.example.lab2.repository.BookingRepository;
import com.example.lab2.repository.ReviewRepository;
import com.example.lab2.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReviewCreator {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    public Optional<Review> createReview(Review review) {

        if (review.getRate() < 1 || review.getRate() > 10) 
            return Optional.ofNullable(null);

        Optional<Booking> booking = bookingRepository.findById(review.getBookingId());

        if (booking.isEmpty())
            return Optional.ofNullable(null);

        review.setPostId(booking.get().getBookingDetails().getPostId());

        Optional<Review> oldReview = reviewRepository.findByBookingId(booking.get().getId());
        if (oldReview.isPresent() && 
            oldReview.get().getReviewStatus() != PublicationStatus.DENIED)
            return Optional.ofNullable(null);

        Optional<User> user = userRepository.findById(review.getAuthorId());

        if (user.isEmpty())
            return Optional.ofNullable(null);
        
        if (oldReview.isPresent())
            review.setId(oldReview.get().getId());
            
        return Optional.ofNullable(reviewRepository.save(newReview(review, user.get())));
    }

    public Review newReview(Review review, User author) {
        return Review.builder()
            .id(review.getId())
            .authorId(review.getAuthorId())
            .postId(review.getPostId())
            .authorName(author.getSurname() + " " + author.getName())
            .bookingId(review.getBookingId())
            .description(review.getDescription())
            .rate(review.getRate())
            .build();
    }
}
