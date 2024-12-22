package com.example.lab2;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.example.lab2.entity.Review;
import com.example.lab2.entity.User;
import com.example.lab2.entity.User.UserRole;
import com.example.lab2.entity.booking.Booking;
import com.example.lab2.entity.booking.BookingDetails;
import com.example.lab2.entity.booking.Booking.BookingStatus;
import com.example.lab2.entity.post.Address;
import com.example.lab2.entity.post.Land;
import com.example.lab2.entity.post.Post;
import com.example.lab2.repository.BookingRepository;
import com.example.lab2.service.administration.AdminService;
import com.example.lab2.service.booking.BookingService;
import com.example.lab2.service.post.PostService;
import com.example.lab2.service.review.ReviewService;
import com.example.lab2.service.user.UserService;

import lombok.RequiredArgsConstructor;

/**
 * loads test data
 */
@Component
@RequiredArgsConstructor
public class DataLoader implements ApplicationRunner {

    private final UserService userService;
    private final PostService postService;
    private final BookingService bookingService;
    private final ReviewService reviewService;
    private final AdminService adminService;

    private final BookingRepository bookingRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        loadUserData();
        loadPostData();
        loadBookingData();
        loadReviewData();
    }

    private void loadUserData() {
        User[] users = {
            User.builder()
                .email("adwdr@gmail.com")
                .password("asd11213")
                .name("Ivan")
                .surname("Ovcharenki")
                .cardCode("1111111111111111")
                .build(),

            User.builder()
                .email("mmmmm@gmail.com")
                .password("asd34213")
                .name("Petro")
                .surname("Pismenniy")
                .cardCode("1111112311111111")
                .build(),

            User.builder()
                .email("admin1")
                .password("admin1")
                .name(null)
                .surname(null)
                .cardCode(null)
                .userRole(UserRole.ADMIN)
                .build(),

            User.builder()
                .email("admin2")
                .password("admin2")
                .name(null)
                .surname(null)
                .cardCode(null)
                .userRole(UserRole.ADMIN)
                .build(),
        };
        
        for (User user : users) {
            userService.createUser(user);
        }
    }

    /**
    * loads test photo
    */
    @SuppressWarnings("null")
    private MultipartFile getPhoto() {
        ClassPathResource resource = new ClassPathResource("static/images/" + "example.jpeg");
        
        if (!resource.exists()) 
            throw new RuntimeException("file not exists");

        try {
            return new MockMultipartFile(
                resource.getFilename(),     // file name
                resource.getFilename(),     // original file name
                "image/jpeg",   // MIME-type
                resource.getInputStream()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private void loadPostData() {
        Address[] addresses = {
            new Address(
                "133", 
                "Kvitkova", 
                "Kyiv", 
                "Kyivska Oblast", 
                "04219"),
            new Address(
                "134", 
                "Kvitkova", 
                "Kyiv", 
                "Kyivska Oblast", 
                "04219"),
            new Address(
                "135", 
                "Kvitkova", 
                "Kyiv", 
                "Kyivska Oblast", 
                "04219"),
            new Address(
                    "136", 
                    "Kvitkova", 
                    "Kyiv", 
                    "Kyivska Oblast", 
                    "04219"),
        };
        
        Land[] lands = {
            new Land(
                1L, 
                "Nice House", 
                "This is Nice House", 
                100, 
                addresses[0], 
                Land.LandType.HOUSE),
            new Land(
                1L, 
                "Nice APARTMENT", 
                "This is new APARTMENT", 
                50, 
                addresses[1], 
                Land.LandType.HOUSE),
            new Land(
                1L, 
                "Nice APARTMENT", 
                "This is Nice APARTMENT", 
                20, 
                addresses[2], 
                Land.LandType.APARTMENT),
            new Land(
                    1L, 
                    "Nice APARTMENT", 
                    "This is APARTMENT", 
                    20, 
                    addresses[3], 
                    Land.LandType.APARTMENT),
        };
              
        Post[] posts = {
            Post.builder()
                .land(lands[0])
                .costPerDay(50f)
                .minDate(LocalDate.of(2004, 10, 2))
                .maxDate(LocalDate.of(2030, 10, 2))
                .build(),

            Post.builder()
                .land(lands[1])
                .costPerDay(25f)
                .minDate(LocalDate.of(2004, 10, 2))
                .maxDate(LocalDate.of(2026, 10, 2))
                .build(),

            Post.builder()
                .land(lands[2])
                .costPerDay(40f)
                .minDate(LocalDate.of(2004, 2, 2))
                .maxDate(LocalDate.of(2026, 12, 2))
                .build(),

            Post.builder()
                .land(lands[3])
                .costPerDay(100f)
                .minDate(LocalDate.of(2004, 2, 2))
                .maxDate(LocalDate.of(2026, 12, 2))
                .build(),
        };
       
        for (Post post : posts) {
            Optional<Post> result = postService.createPost(post, getPhoto());

            if (result.isEmpty())
                continue;

            Post newPost = result.get();
            adminService.acceptPost(newPost.getId());
        }      
    }

    private void loadBookingData() {
        BookingDetails[] bookingDetails = {
            new BookingDetails(
                1L, 
                2L, 
                LocalDate.of(2024, 12, 30), 
                LocalDate.of(2025, 10, 2)),
        };

        for (BookingDetails details : bookingDetails) {
            bookingService.makeBooking(details);
        }

        Booking[] bookings = {
            Booking.builder()
                .bookingDate(LocalDate.of(2020, 1, 2))
                .bookingDetails(new BookingDetails(
                    4L, 
                    2L, 
                    LocalDate.of(2020, 2, 2), 
                    LocalDate.of(2020, 10, 2)))
                .status(BookingStatus.CLOSED)
                .totalCost(25000.00F)
                .build(),
        };
        for (var booking : bookings) {
            if (!bookingRepository.existsByBookingDetails(booking.getBookingDetails())) {
                bookingRepository.save(booking);
            }
        }
    }

    private void loadReviewData() {
        Review[] reviews = {
            // Review.builder()
            //     .authorId(2L)
            //     .bookingId(2L)
            //     .description("All is nice")
            //     .rate((byte)10)
            //     .build(),
            Review.builder()
                .authorId(2L)
                .bookingId(1L)
                .description("All is cool")
                .rate((byte)9)
                .build()
            
        };

        for (Review review : reviews) {
            
            Optional<Review> result = reviewService.createReview(review);

            if (result.isEmpty())
                continue;

            Review newReview = result.get();
            adminService.acceptReview(newReview.getId());
        }
    }
}
