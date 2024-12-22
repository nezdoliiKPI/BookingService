package com.example.lab2.service.review;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.lab2.entity.PublicationStatus;
import com.example.lab2.entity.Review;
import com.example.lab2.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;

    private final ReviewCreator reviewCreator;

    public Optional<Review> createReview(Review review) {
        return reviewCreator.createReview(review);
    }

    public List<Review> getPostReviews(Long postId) {
        return (List<Review>) reviewRepository.findAllByPostId(postId);
    }

    public List<Review> getPostAvaliableReviews(Long postId) {
        List<Review> lst = new ArrayList<>();

        for (Review review : reviewRepository.findAllByPostId(postId)) {
            if (review.getReviewStatus() == PublicationStatus.AVAILABLE) {
                lst.add(review);
            }
        }

        return lst;
    }

    public Optional<Review> getReview(Long id) {
        return reviewRepository.findById(id);
    }

    public Optional<Review> getBookingReview(Long bookingId) {
        return reviewRepository.findByBookingId(bookingId);
    }

    /////////////////////////////////////////////////
    /// Test
    public boolean deleteReview(Long id) {
        reviewRepository.deleteById(id);
        return true;
    }

    public List<Review> findAll() {
        return (List<Review>) reviewRepository.findAll();
    }
}
