package com.example.lab2.service.post;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.lab2.controller.specifications.PostFilter;
import com.example.lab2.controller.specifications.PostSpecifications;
import com.example.lab2.entity.PublicationStatus;
import com.example.lab2.entity.post.Post;
import com.example.lab2.repository.PostRepository;

import org.springframework.core.io.Resource;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    private final PostUpdater postUpdater;
    private final PostCreator postCreator;
    private final PostDeleter postDeleter;
    private final PhotosComponent photosComponent;

    @Transactional(rollbackFor = Exception.class)
    public Optional<Post> createPost(Post post, MultipartFile ...photos) {
        final Long uniqueNumb = postRepository.count() + 1;
        final List<String> keys = new ArrayList<>();

        for (MultipartFile photo : photos) {
            Optional<String> imageKey = photosComponent.savePostPhoto(uniqueNumb, photo);

            if (imageKey.isEmpty())
                return Optional.ofNullable(null);

            keys.add(imageKey.get());         
        }
        
        post.setImageKeys(keys);
        return postCreator.createPost(post);
    }

    @Transactional(rollbackFor = Exception.class)
    public Optional<Post> updatePost(Post post, @Nullable MultipartFile ...photos) {
        if (photos == null) 
            return postUpdater.updatePost(post);

        final Long uniqueNumb = post.getId();
        final List<String> keys = new ArrayList<>();

        for (MultipartFile photo : photos) {
            Optional<String> imageKey = photosComponent.savePostPhoto(uniqueNumb, photo);

            if (imageKey.isEmpty())
                return Optional.ofNullable(null);

            keys.add(imageKey.get());         
        }

        post.setImageKeys(keys);
        return postUpdater.updatePost(post);
    }

    public List<Post> getPostList(Long id) {
        final List<Post> allList = (List<Post>) postRepository.findAllByLandOwnerId(id);
        final List<Post> list = new ArrayList<>();

        for (Post post : allList)
            if (post.getPostStatus() != PublicationStatus.ON_DELETE)
                list.add(post);

        return list;
    }

    public boolean deletePost(Long postId) {
        return postDeleter.deletePost(postId);
    }

    public List<Post> getPage(@Nullable Specification<Post> spec, Pageable pageable, PostFilter filter) {
        if (!filter.getSortingType().equals("rate")) 
            return postRepository.findAll(spec, pageable).getContent();

        int number = pageable.getPageNumber();
        int size = pageable.getPageSize();

        List<Post> result = postRepository.findAll(spec);

        result = result.stream()
                .skip(number * size) 
                .limit(number * size + size) 
                .collect(Collectors.toList());

        Comparator<Post> scoreComparator = Comparator
            .comparing(Post::getScore, Comparator.nullsFirst(Comparator.naturalOrder()))
            .reversed();

        result.sort(scoreComparator);

        return result;
    }

    //@Scheduled(fixedDelay = 1, initialDelay = 1, timeUnit = TimeUnit.MINUTES)
    @Scheduled(fixedDelay = 30, timeUnit = TimeUnit.SECONDS) 
    public void updatePostsScore() {
        List<Post> all = findAll();

        for (Post post : all) {
            postUpdater.updatePostScore(post.getId());
        }
    }

    public List<Resource> getPostPhotos(Long postId) {
        final List<Resource> list = new ArrayList<>();
        final Optional<Post> result = postRepository.findById(postId);

        if (result.isPresent())
            for (String key : result.get().getImageKeys())
                list.add(photosComponent.getPostPhoto(key).get());

        return list;
    }

    public Optional<Resource> getPostPhoto(String key) {
        return photosComponent.getPostPhoto(key);
    }

    /////////////////////////////////////////////////
    /// Test
    public List<Post> findAll() {
        return (List<Post>) postRepository.findAll();
    }

    public Optional<Post> findPost(Long id) {
        return postRepository.findById(id);
    }

    public List<Post> findAll(PostFilter filter) {
        return postRepository.findAll(PostSpecifications.fromFilter(filter));
    }
}
