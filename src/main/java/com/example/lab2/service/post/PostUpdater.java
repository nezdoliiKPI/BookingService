package com.example.lab2.service.post;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.example.lab2.entity.PublicationStatus;
import com.example.lab2.entity.Review;
import com.example.lab2.entity.booking.Booking;
import com.example.lab2.entity.booking.Booking.BookingStatus;
import com.example.lab2.entity.post.Post;
import com.example.lab2.repository.BookingRepository;
import com.example.lab2.repository.PostRepository;
import com.example.lab2.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PostUpdater {
    private final PostRepository postRepository;
    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;

    public Optional<Post> updatePost(Post post) {
        if (postRepository.findById(post.getId()).isEmpty())
            return Optional.ofNullable(null);

        Post oldPost = postRepository.findById(post.getId()).get();

        //check status
        if (oldPost.getPostStatus() == PublicationStatus.DENIED ||
            oldPost.getPostStatus() == PublicationStatus.UNDER_REVIEW) {
                post.setPostStatus(PublicationStatus.UNDER_REVIEW);
        }
        else 
        if (
            !(post.getPostStatus() == PublicationStatus.AVAILABLE || 
            isUpdateablePostStatus(post.getId(), PublicationStatus.AVAILABLE))
        ) {
            post.setScore(oldPost.getScore());
        }
                    
        //check owner
        if (!(oldPost.getLand().getOwnerId().equals(post.getLand().getOwnerId()))) 
            return Optional.ofNullable(null);
        //check housing  
        if (!(oldPost.getLand().getAddress().equals(post.getLand().getAddress()))) 
            return Optional.ofNullable(null);

        return Optional.of(postRepository.save(post));
    }

    public void updatePostScore(Long postId) {
        List<Review> reviews = (List<Review>)reviewRepository.findAllByPostId(postId);

        if (reviews.isEmpty())
            return;
        
        int total = 0;
        int num = 0;
        for (Review review : reviews) {
            total+= review.getRate();
            num++;  
        }
 
        if(total == 0 || postRepository.findById(postId).isEmpty())
            return;         
        Post post = postRepository.findById(postId).get();
      
        post.setScore((byte)(total/num));
        postRepository.save(post);
    }

    public boolean isUpdateablePostStatus(Long postId, PublicationStatus status) {

        if (postRepository.findById(postId).isEmpty())
            return false;

        Post post = postRepository.findById(postId).get();

        if (post.getPostStatus() == status)
            return true;
        if (post.getPostStatus() == PublicationStatus.ON_DELETE)
            return false;

        if (status != PublicationStatus.AVAILABLE && 
            status != PublicationStatus.UNAVAILABLE) 
            return false;
        if (status == PublicationStatus.UNAVAILABLE)
            if (postHasCurrentBooking(post.getId()))
                return false;

        return true;
    }

    public boolean postHasCurrentBooking(Long postId) {
        List<Booking> bookings = (List<Booking>)bookingRepository.findAllByBookingDetailsPostId(postId);
        for (Booking booking : bookings) 
            if (booking.getStatus() == BookingStatus.ACTIVE ||
                booking.getStatus() == BookingStatus.PROCESSED) 
                return true;
        return false;
    }
}
