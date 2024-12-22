package com.example.lab2.service.post;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.example.lab2.entity.post.Post;
import com.example.lab2.repository.PostRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PostDeleter {
    private final PostRepository postRepository;
    private final PostUpdater postUpdater;

    public boolean deletePost(Long postId) {
        Optional<Post> getResult = postRepository.findById(postId);  

        if(getResult.isEmpty())
            return false;

        Post post = getResult.get();

        if (postUpdater.postHasCurrentBooking(post.getId()))
            return false;     
        if (post.getScore() == Post.POST_NOT_SCORED) {
            postRepository.deleteById(postId);
            return true;
        }

        post.setPostStatus(com.example.lab2.entity.PublicationStatus.ON_DELETE);
        postRepository.save(post);
        return true;
    }
}
