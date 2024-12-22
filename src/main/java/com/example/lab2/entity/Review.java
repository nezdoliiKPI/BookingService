package com.example.lab2.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reviews")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Review {

    @Id
    @Builder.Default
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "review_id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id = null;

    @Column(name = "author_id")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long authorId;

    @Builder.Default
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "post_id")
    private Long postId = null;

    @Column(name = "booking_id")
    private Long bookingId;
    
    @Column(name = "author_name")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String authorName;

    private String description;

    @Min(1)@Max(10)
    private Byte rate;

    @Builder.Default
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "review_status")
     @Enumerated(EnumType.STRING)
    private PublicationStatus reviewStatus = PublicationStatus.UNDER_REVIEW;
}
