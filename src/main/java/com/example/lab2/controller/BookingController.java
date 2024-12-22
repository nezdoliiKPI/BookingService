package com.example.lab2.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.lab2.entity.booking.Booking;
import com.example.lab2.entity.booking.BookingDetails;
import com.example.lab2.security.AuthenticationHandler;
import com.example.lab2.security.UserDetailsImpl;
import com.example.lab2.service.booking.BookingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/booking")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @GetMapping("")
    public ResponseEntity<Booking> getBooking(@RequestParam Long id) {
        final Optional<Booking> result = bookingService.getBooking(id);

        if(result.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        Booking booking = result.get(); 

        Authentication authentication = 
            SecurityContextHolder.getContext().getAuthentication();

        if (!AuthenticationHandler.mayUser(authentication)
                .hasUserRole()
                .isOwner(booking.getBookingDetails().getUserId())
                .or()
                .hasAdminRole()
                .handle()
        ) 
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        return new ResponseEntity<>(booking, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<Booking> createBooking(@RequestBody BookingDetails bookingDetails) {    
        Authentication authentication = 
            SecurityContextHolder.getContext().getAuthentication();

        Long userId = ((UserDetailsImpl)authentication.getPrincipal()).getId();
        bookingDetails.setUserId(userId);

        return bookingService.makeBooking(bookingDetails)
            .map(booking -> new ResponseEntity<>(booking, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.CONFLICT));
    }

    @GetMapping("/list")
    public ResponseEntity<List<Booking>> getBookingList() {
        Authentication authentication = 
            SecurityContextHolder.getContext().getAuthentication();

        Long userId = ((UserDetailsImpl)authentication.getPrincipal()).getId();

        if (!AuthenticationHandler.mayUser(authentication)
                .hasUserRole()
                .isOwner(userId)
                .or()
                .hasAdminRole()
                .handle()
        ) 
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        final List<Booking> bookingList = bookingService.getBookingList(userId);

        return !bookingList.isEmpty()
           ? new ResponseEntity<>(bookingList, HttpStatus.OK)
           : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
