package com.example.lab2.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.lab2.entity.Review;
import java.util.Optional;
import com.example.lab2.entity.PublicationStatus;

public interface ReviewRepository extends JpaRepository<Review, Long> { 
    Iterable<Review> findAllByPostId(Long id);
    Iterable<Review> findAllByReviewStatus(PublicationStatus reviewStatus);

    Optional<Review> findByIdAndReviewStatus(Long id, PublicationStatus reviewStatus);

    Optional<Review> findByBookingId(Long bookingId);

    boolean existsByBookingId(Long bookingId);
}
