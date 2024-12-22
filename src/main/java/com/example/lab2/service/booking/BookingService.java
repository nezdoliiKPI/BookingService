package com.example.lab2.service.booking;

import com.example.lab2.entity.booking.Booking;
import com.example.lab2.entity.booking.BookingDetails;
import com.example.lab2.entity.booking.Booking.BookingStatus;
import com.example.lab2.repository.BookingRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final BookingCreator bookingCreator;

    public Optional<Booking> makeBooking(BookingDetails bookingDetails) {
        return bookingCreator.createBooking(bookingDetails);    
    }

    public List<Booking> getBookingList(Long userId) {
        List<Booking> bookings = 
            (List<Booking>) bookingRepository.findAllByBookingDetailsUserId(userId);

        bookings.sort(
            (b1, b2) -> b1.getBookingDate().compareTo(b2.getBookingDate())
        );
        
        return bookings;
    }
    
    public Optional<Booking> getBooking(Long id) {
        return bookingRepository.findById(id);
    }

    @Scheduled(fixedDelay = 4, timeUnit = TimeUnit.HOURS)
    public void updateBookingsSatus() {
        List<Booking> bookings = null;
        
        bookings = (List<Booking>)
            bookingRepository.findAllByStatus(BookingStatus.ACTIVE);

        for (Booking booking : bookings) {
            LocalDate endDate = booking.getBookingDetails().getEndDate();

            if (endDate.isBefore(LocalDate.now())) {
                booking.setStatus(BookingStatus.CLOSED);
                bookingRepository.save(booking);
            }
        }

        bookings = (List<Booking>)
            bookingRepository.findAllByStatus(BookingStatus.PROCESSED);

        for (Booking booking : bookings) {
            LocalDate startDate = booking.getBookingDetails().getEndDate();
            
            if (startDate.isBefore(LocalDate.now())) {
                booking.setStatus(BookingStatus.ACTIVE);
                bookingRepository.save(booking);
            }
        }
    }

    /////////////////////////////////////////////////
    /// Test
    public List<Booking> findAll() {
        return bookingRepository.findAll();
    }

    // public boolean deleteBooking(Long id) {
    //     bookingRepository.deleteById(id);
    //     return true;
    // }
}
