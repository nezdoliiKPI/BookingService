package com.example.lab2.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.lab2.controller.specifications.PostFilter;
import com.example.lab2.controller.specifications.PostSpecifications;
import com.example.lab2.entity.PublicationStatus;
import com.example.lab2.entity.Report;
import com.example.lab2.entity.post.Post;
import com.example.lab2.security.AuthenticationHandler;
import com.example.lab2.security.UserDetailsImpl;
import com.example.lab2.service.administration.ReportComponent;
import com.example.lab2.service.post.PostService;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final ReportComponent reportComponent;

    @PutMapping("/posts/page")
    public ResponseEntity<List<Post>> getPage(
        @RequestParam(name = "page-number") int number,
        @RequestParam(name = "page-size") int size,
        @RequestBody PostFilter filter
    ) {      
        Authentication authentication = 
            SecurityContextHolder.getContext().getAuthentication();

        Long userId = null;

        if (authentication != null &&
            authentication.isAuthenticated() &&
            //when Anonymous Authentication is enabled
            !(authentication instanceof AnonymousAuthenticationToken)
        )
        userId = ((UserDetailsImpl)authentication.getPrincipal()).getId();

        filter.setUserId(userId);

        var spec = PostSpecifications
            .fromFilter(filter)
            .and(PostSpecifications.hasStatus(PublicationStatus.AVAILABLE));

        final List<Post> posts = 
            postService.getPage(spec, filter.getPageable(number, size), filter);

        return !posts.isEmpty()
           ? new ResponseEntity<>(posts, HttpStatus.OK)
           : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("")
    public ResponseEntity<Post> getPost(@RequestParam long id) {
        final Optional<Post> result = postService.findPost(id);

        if(result.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        
        Post post = result.get();

        if (post.getPostStatus() == PublicationStatus.AVAILABLE)
            return new ResponseEntity<>(post, HttpStatus.OK);

        Authentication authentication = 
            SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null &&
            authentication.isAuthenticated() &&
            //when Anonymous Authentication is enabled
            authentication instanceof AnonymousAuthenticationToken 
        ) 
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        if (post.getPostStatus() == PublicationStatus.UNAVAILABLE) {
            if (AuthenticationHandler.mayUser(authentication)
                    .hasUserRole()
                    .or()
                    .hasAdminRole()
                    .handle()
            ) return new ResponseEntity<>(post, HttpStatus.OK);
        }

        if (post.getPostStatus() == PublicationStatus.UNAVAILABLE || 
            post.getPostStatus() == PublicationStatus.DENIED || 
            post.getPostStatus() == PublicationStatus.UNDER_REVIEW)
            if (AuthenticationHandler.mayUser(authentication)
                    .hasUserRole()
                    .isOwner(post.getOwnerId())
                    .or()
                    .hasAdminRole()
                    .handle()
            ) return new ResponseEntity<>(post, HttpStatus.OK);
        

        if (post.getPostStatus() == PublicationStatus.ON_DELETE)
            if (AuthenticationHandler.mayUser(authentication)
                    .hasAdminRole()
                    .handle()
            ) return new ResponseEntity<>(post, HttpStatus.OK);

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/list")
    public ResponseEntity<List<Post>> getPostList() {
        Authentication authentication = 
            SecurityContextHolder.getContext().getAuthentication();

        Long userId = ((UserDetailsImpl)authentication.getPrincipal()).getId();

        final List<Post> postList = postService.getPostList(userId);

        return !postList.isEmpty()
           ? new ResponseEntity<>(postList, HttpStatus.OK)
           : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/create")
    @CrossOrigin
    public ResponseEntity<?> createPost(
        @RequestPart("data") @NotNull Post postForm, 
        @RequestPart("files") @NotNull @Size(min=1, max=10) MultipartFile[] photos
    ) {
        //check photos
        for (MultipartFile photo : photos) {
            String contentType = photo.getContentType();

            if (!("image/jpeg".equals(contentType) || 
                    "image/jpg".equals(contentType))) {
                return new ResponseEntity<>(
                    "Photos should have jpeg or jpg format", 
                    HttpStatus.BAD_REQUEST
                );
            }
        }

        Authentication authentication = 
            SecurityContextHolder.getContext().getAuthentication();

        Long userId = ((UserDetailsImpl)authentication.getPrincipal()).getId();
        postForm.getLand().setOwnerId(userId);

        return postService.createPost(postForm, photos)
            .map(post -> new ResponseEntity<>(post, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.CONFLICT));
    }

    @PutMapping("/update")
    public ResponseEntity<?> updatePost(
        @RequestPart("data") @NotNull Post postForm, 
        @RequestPart("files") @Nullable @Size(min=1, max=10) MultipartFile[] photos
    ) {
        final Optional<Post> result = postService.findPost(postForm.getId());
        
        if (result.isEmpty()) 
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        postForm.setImageKeys(result.get().getImageKeys());

        Authentication authentication = 
            SecurityContextHolder.getContext().getAuthentication();
        
        if (!AuthenticationHandler.mayUser(authentication)
                .hasUserRole()
                .isOwner(result.get().getOwnerId())
                .handle()
        ) return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        //check photos
        if (photos != null) {
            for (MultipartFile photo : photos) {
                String contentType = photo.getContentType();
    
                if (!("image/jpeg".equals(contentType) || 
                    "image/jpg".equals(contentType))) {
                    return new ResponseEntity<>(
                        "Photos should have jpeg or jpg format", 
                        HttpStatus.BAD_REQUEST
                    );
                }
            }
        }
        
        Long userId = ((UserDetailsImpl)authentication.getPrincipal()).getId();
        postForm.getLand().setOwnerId(userId);

        return postService.updatePost(postForm, photos)
            .map(updatedPost -> new ResponseEntity<>(updatedPost, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.CONFLICT));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deletePost(@RequestParam Long id) {
        final Optional<Post> result = postService.findPost(id);

        if (result.isEmpty()) 
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        Post post = result.get();

        Authentication authentication = 
            SecurityContextHolder.getContext().getAuthentication();
        
        if (!AuthenticationHandler.mayUser(authentication)
                .hasUserRole()
                .isOwner(post.getOwnerId())
                .or()
                .hasAdminRole()
                .handle()
        ) return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        return postService.deletePost(id)
           ? new ResponseEntity<>(HttpStatus.OK)
           : new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    @GetMapping("/photo")
    public ResponseEntity<?> downloadPhotos(@NotNull @RequestParam String key) {
        Optional<Resource> image = postService.getPostPhoto(key);

        return  image.isPresent()
            ? ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image.get())
            : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/post-report")
    public ResponseEntity<?> getReport(@NotNull @RequestParam("post-id") Long postId) {
        final Optional<Post> result = postService.findPost(postId);

        if (result.isEmpty()) 
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        Post post = result.get();

        Authentication authentication = 
            SecurityContextHolder.getContext().getAuthentication();
        
        if (!AuthenticationHandler.mayUser(authentication)
                .hasUserRole()
                .isOwner(post.getOwnerId())
                .or()
                .hasAdminRole()
                .handle()
        ) return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        Optional<Report> report = reportComponent.getPostReport(postId);

        return  report.isPresent()
            ? new ResponseEntity<>(report.get(), HttpStatus.OK)
            : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
