package com.example.lab2.service.user;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.lab2.entity.PublicationStatus;
import com.example.lab2.entity.User;
import com.example.lab2.entity.User.UserStatus;
import com.example.lab2.entity.post.Post;
import com.example.lab2.repository.PostRepository;
import com.example.lab2.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    private final UserCreator userCreator;
    private final UserUpdater userUpdater;

    public Optional<User> createUser(User userForm) {
        return userCreator.createUser(userForm);
    }

    public Optional<User> updateUser(User user) {
        return userUpdater.updateUser(user);
    }

    public boolean deleteUser(Long id) {
        Optional<User> result = userRepository.findById(id);

        if (result.isEmpty())
            return false;

        User user = result.get();

        var postList = (List<Post>) postRepository
            .findAllByLandOwnerId(user.getUserId());

        postList.forEach(post -> {
            post.setPostStatus(PublicationStatus.UNAVAILABLE);
            postRepository.save(post);
        });
        
        // if (postList.isEmpty()) {
        //     userRepository.deleteById(user.getUserId());
        //     return true;
        // }

        // //set all posts not avaliable  
        // for (Post post : postList) 
        //     post.setPostStatus(PublicationStatus.ON_DELETE);
        // postRepository.saveAllAndFlush(postList);

        //set ondelete status
        user.setUserStatus(UserStatus.ON_DELETE);
        userRepository.saveAndFlush(user);
        return true;
    }

    public Optional<User> getUser(long id) {
        return userRepository.findById(id);
    }

    //@Scheduled(fixedDelay = 10, initialDelay = 2, timeUnit = TimeUnit.MINUTES)
    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
    public void updateUsersScore() {
        List<User> all = findAll();

        for (User user : all) {
            if (user.getUserStatus() == UserStatus.ACTIVE) 
                userUpdater.updateOwnerScore(user.getUserId());
        }
    }

    /////////////////////////////////////////////////
    /// Test
    public List<User> findAll() {
        return (List<User>)(userRepository.findAllByUserStatus(UserStatus.ACTIVE));
    } 
}   
