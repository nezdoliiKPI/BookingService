package com.example.lab2.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.lab2.entity.booking.Booking;
import com.example.lab2.entity.booking.Booking.BookingStatus;
import com.example.lab2.entity.booking.BookingDetails;


public interface BookingRepository extends JpaRepository<Booking, Long> {
    Iterable<Booking> findAllByBookingDetailsUserId(Long id);
    Iterable<Booking> findAllByStatus(BookingStatus status);

    boolean existsByBookingDetails(BookingDetails bookingDetails);
    boolean existsByBookingDetailsUserId(Long id);

    Iterable<Booking> findAllByBookingDetailsPostIdAndBookingDetailsUserId(Long postId, Long userId);
    Iterable<Booking> findAllByBookingDetailsPostId(Long postId);
}
