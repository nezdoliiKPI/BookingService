package com.example.lab2.service.user;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.example.lab2.entity.User;
import com.example.lab2.entity.post.Post;
import com.example.lab2.repository.PostRepository;
import com.example.lab2.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserUpdater {
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public Optional<User> updateUser(User user) {
        Optional<User> getResult = userRepository.findById(user.getUserId());
        
        if(getResult.isEmpty())
            return Optional.ofNullable(null);

        User oldUser = getResult.get();

        if (oldUser.equals(user)) 
            return Optional.of(oldUser);

        //password check
        if (!oldUser.getPassword().equals(user.getPassword()) &&
            userRepository.existsByPassword(user.getPassword()))
            return Optional.ofNullable(null);

        //email check
        if (!oldUser.getEmail().equals(user.getEmail()) &&
            userRepository.existsByEmail(user.getEmail()))
            return Optional.ofNullable(null);
                
        user.setOwnerScore(oldUser.getOwnerScore());
        user.setUserRole(oldUser.getUserRole());

        return Optional.of(userRepository.save(user));
    }

    public void updateOwnerScore(Long userId) {
        List<Post> posts = (List<Post>)postRepository
            .findAllByLandOwnerId(userId);

        if (posts.isEmpty())
            return;
        
        int total = 0;
        int num = 0;
        for (Post post : posts) {
            if (post.getScore() != Post.POST_NOT_SCORED &&
                post.isAvailable()    
            ) {
                total+= post.getScore();
                num++;
            }  
        }

        Optional<User> getResult = userRepository.findById(userId);  
        if(total == 0 || getResult.isEmpty())
            return;
        User user = getResult.get();

        user.setOwnerScore((byte)(total/num));
        userRepository.save(user);  
    }
}
