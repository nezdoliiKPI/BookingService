package com.example.lab2.service.post;

import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.example.lab2.entity.post.Post;
import com.example.lab2.repository.PostRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PostCreator {
    private final PostRepository postRepository;

    public Optional<Post> createPost(Post post) {
        //check rent period
        if (ChronoUnit.DAYS.between(post.getMinDate(), post.getMaxDate()) < 1)
            return Optional.ofNullable(null);

        if (post.getCostPerDay() <= 0)
            return Optional.ofNullable(null);

        //check unique address
        if (postRepository.existsByLandAddress(post.getLand().getAddress())) 
            return Optional.ofNullable(null);
        
        return Optional.ofNullable(postRepository.save(newPost(post)));
    }

    public Post newPost(Post post) {
        return Post.builder()
            .costPerDay(post.getCostPerDay())
            .land(post.getLand())      
            .minDate(post.getMinDate())
            .maxDate(post.getMaxDate())
            .imageKeys(post.getImageKeys())
            .build();
    }
}
