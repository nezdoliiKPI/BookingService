package com.example.lab2.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.example.lab2.entity.PublicationStatus;
import com.example.lab2.entity.post.Address;
import com.example.lab2.entity.post.Land;
import com.example.lab2.entity.post.Post;


public interface PostRepository extends JpaRepository<Post,Long>, JpaSpecificationExecutor<Post> {
    boolean existsByLand(Land land);
    boolean existsByLandAddress(Address address);
    
    boolean existsByIdAndPostStatus(Long id, PublicationStatus postStatus);
    boolean existsByIdAndLandOwnerId(Long postId, Long ownerId);

    Iterable<Post> findAllByLandOwnerId(Long id);

    Iterable<Post> findAllByLandAddress(Address address);

    Iterable<Post> findAllByLandOwnerIdAndPostStatus(Long ownerId, PublicationStatus postStatus);
    Optional<Post> findByIdAndPostStatus(Long id, PublicationStatus postStatus);
}
