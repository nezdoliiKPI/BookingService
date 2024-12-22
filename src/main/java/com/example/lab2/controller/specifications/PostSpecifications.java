package com.example.lab2.controller.specifications;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

import com.example.lab2.entity.PublicationStatus;
import com.example.lab2.entity.post.Land;
import com.example.lab2.entity.post.Post;
import com.example.lab2.entity.post.Land.LandType;

@SuppressWarnings("unused")
public class PostSpecifications {
    
    public static Specification<Post> fromFilter(PostFilter filter) {
        return Specification
                .where(hasType(filter.getType()))
                .and(locatesIn(filter.getCity()))

                .and(hasMinCost(filter.getMinCost()))
                .and(hasMaxCost(filter.getMaxCost()))

                .and(hasMinArea(filter.getMinArea()))
                .and(hasMaxArea(filter.getMaxArea()))
                
                .and(hasStartDateBefore(filter.getMinDate()))
                .and(hasEndDateAfter(filter.getMaxDate()))
                
                .and(excludeOwner(filter.getUserId()));
    }

    public static Specification<Post> hasType(LandType type) {
        return (root, query, builder) -> 
            type != null 
                ? builder.equal(root.get("land").get("type"), type)
                : null;
    }

    public static Specification<Post> excludeOwner(Long ownerId) {
        return (root, query, builder) -> 
            ownerId != null 
                ? builder.notEqual(root.get("land").get("ownerId"), ownerId)
                : null;
    }

    public static Specification<Post> locatesIn(String city) {
        return (root, query, builder) -> 
            city != null 
                ? builder.equal(root
                    .get("land")
                    .get("address")
                    .get("city"), city)
                : null;
    }

    public static Specification<Post> hasMinCost(Float costPerDay) {
        return (root, query, builder) -> 
            costPerDay != null 
                ? builder.greaterThanOrEqualTo(root.get("costPerDay"), costPerDay)
                : null;
    }

    public static Specification<Post> hasMaxCost(Float costPerDay) {
        return (root, query, builder) -> 
            costPerDay != null 
                ? builder.lessThanOrEqualTo(root.get("costPerDay"), costPerDay)
                : null;
    }

    public static Specification<Post> hasMinArea(Short area) {
        return (root, query, builder) -> 
            area != null 
                ? builder.greaterThanOrEqualTo(root.get("land").get("totalArea"), area)
                : null;
    }

    public static Specification<Post> hasMaxArea(Short area) {
        return (root, query, builder) -> 
            area != null 
                ? builder.lessThanOrEqualTo(root.get("land").get("totalArea"), area)
                : null;
    }

    public static Specification<Post> isAvailable(Boolean avaliable) {
        return (root, query, builder) -> 
            avaliable != null 
                    ? builder.equal(root.get("available"), avaliable)
                    : null;
    }

    public static Specification<Post> hasStatus(PublicationStatus status) {
        return (root, query, builder) -> 
            status != null 
                    ? builder.equal(root.get("postStatus"), status)
                    : null;
    }

    public static Specification<Post> hasNotStatus(PublicationStatus status) {
        return (root, query, builder) -> 
            status != null 
                    ? builder.notEqual(root.get("postStatus"), status)
                    : null;
    }

    public static Specification<Post> hasStartDateBefore(LocalDate startDate) {
        return (root, query, criteriaBuilder) -> 
            startDate != null
                    ? criteriaBuilder.lessThanOrEqualTo(root.get("minDate"), startDate)
                    : null;
    }

    public static Specification<Post> hasEndDateAfter(LocalDate endDate) {
        return (root, query, criteriaBuilder) -> 
            endDate != null
                    ? criteriaBuilder.greaterThanOrEqualTo(root.get("maxDate"), endDate)
                    : null;
    }
}
