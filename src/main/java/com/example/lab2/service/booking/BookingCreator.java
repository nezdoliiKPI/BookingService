package com.example.lab2.service.booking;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.lab2.entity.PublicationStatus;
import com.example.lab2.entity.booking.Booking;
import com.example.lab2.entity.booking.BookingDetails;
import com.example.lab2.entity.post.Post;
import com.example.lab2.repository.BookingRepository;
import com.example.lab2.repository.PostRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BookingCreator {
    private final BookingRepository bookingRepository;
    private final PostRepository postRepository;

    @Transactional
    Optional<Booking> createBooking(BookingDetails bookingDetails) {

        Optional<Post> checkedPost = getCheckedPost(bookingDetails);

        if (bookingDetails.getStartDate().isBefore(LocalDate.now()) ||
        bookingDetails.getStartDate().isAfter(bookingDetails.getEndDate()))
            return Optional.ofNullable(null);

        if (checkedPost.isEmpty()) 
            return Optional.ofNullable(null);

        Post post = checkedPost.get();

        if (!post.isAvailable())
            return Optional.ofNullable(null); 
        
        post.setPostStatus(PublicationStatus.UNAVAILABLE);
        postRepository.save(post);
            
        return Optional.of(bookingRepository.save(
                newBooking(bookingDetails, post)
        ));
    }

    public Booking newBooking(BookingDetails bookingDetails, Post post) {
        return Booking.builder()
            .bookingDetails(bookingDetails)
            .bookingDate(LocalDate.now())
            .totalCost(bookingDetails.getDays() * post.getCostPerDay())
            .build();
    }

    Optional<Post> getCheckedPost(BookingDetails bookingDetails) {
        Optional<Post> getResult = postRepository.findById(bookingDetails.getPostId());

        if (getResult.isEmpty()) 
            return Optional.ofNullable(null);

        Post post = getResult.get();

        //check selfbooking
        if (post.getLand().getOwnerId().equals(bookingDetails.getUserId())) 
            return Optional.ofNullable(null);

        if (!post.isAvailable() && 
            !bookingDetails.isDataConsistencyTo(post))  
            return Optional.ofNullable(null);

        return Optional.of(post);
    }
}