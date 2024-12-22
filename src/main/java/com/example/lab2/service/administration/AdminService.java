package com.example.lab2.service.administration;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.lab2.controller.specifications.PostFilter;
import com.example.lab2.controller.specifications.PostSpecifications;
import com.example.lab2.entity.PublicationStatus;
import com.example.lab2.entity.Report;
import com.example.lab2.entity.Review;
import com.example.lab2.entity.post.Post;
import com.example.lab2.repository.PostRepository;
import com.example.lab2.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final PostRepository postRepository;
    private final ReviewRepository reviewRepository;
    
    private final ReportComponent reportComponent;
 
    public List<Post> getPosts(int number, int size) {
        PostFilter filter = new PostFilter();

        var spec = PostSpecifications
            .fromFilter(filter)
            .and(PostSpecifications.hasStatus(PublicationStatus.UNDER_REVIEW));

        return postRepository
            .findAll(spec, filter.getPageable(number, size))
                .getContent();
    }

    public List<Review> getReviews(int number, int size) {
        return (List<Review>) reviewRepository.findAllByReviewStatus(PublicationStatus.UNDER_REVIEW);
    }

    public boolean acceptPost(Long postId) {
        Optional<Post> result = postRepository.findById(postId);

        if (result.isEmpty()) 
            return false;
        if (result.get().getPostStatus() != PublicationStatus.UNDER_REVIEW)
            return false;

        Post post = result.get();

        post.setPostStatus(PublicationStatus.AVAILABLE);
        return postRepository.save(post) != null;  
    }

    public boolean acceptReview(Long reviewId) {
        Optional<Review> result = reviewRepository.findById(reviewId);

        if (result.isEmpty()) 
            return false;
        if (result.get().getReviewStatus() != PublicationStatus.UNDER_REVIEW)
            return false;

            Review post = result.get();

        post.setReviewStatus(PublicationStatus.AVAILABLE);
        return reviewRepository.save(post) != null;  
    }

    @Transactional(rollbackFor = Exception.class)
    public Optional<Report> denyPost(Report report) {
        Optional<Post> result = postRepository.findById(report.getPostId());

        if (result.isEmpty()) 
            return Optional.ofNullable(null);
        if (result.get().getPostStatus() != PublicationStatus.UNDER_REVIEW)
            return Optional.ofNullable(null);

        try {
            return reportComponent.createPostReport(report);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return Optional.ofNullable(null);
        }  
    }

    public Boolean denyReview(Long reviewId) {
        Optional<Review> result = reviewRepository.findById(reviewId);

        if (result.isEmpty()) 
            return false;
        if (result.get().getReviewStatus() != PublicationStatus.UNDER_REVIEW)
            return false;

        result.get().setReviewStatus(PublicationStatus.DENIED);
        return reviewRepository.save(result.get()) != null;
    }
}
