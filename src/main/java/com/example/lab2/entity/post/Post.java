package com.example.lab2.entity.post;
import java.time.LocalDate;
import java.util.List;

import com.example.lab2.entity.PublicationStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "posts")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Post {

    @Id
    @Builder.Default
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "post_id")  
    private Long id = null;

    @Embedded
    private Land land;

    @Column(name = "cost_per_day")
    private Float costPerDay;
    
    @Basic
    @Column(name = "min_date")
    private LocalDate minDate;
    
    @Basic
    @Column(name = "max_date")
    private LocalDate maxDate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Builder.Default
    private Byte score = POST_NOT_SCORED;

    @Column(name = "image_keys")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Builder.Default
    private List<String> imageKeys = null;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Builder.Default
    @Column(name = "post_status")
    @Enumerated(EnumType.STRING)
    private PublicationStatus postStatus = PublicationStatus.UNDER_REVIEW;

    @JsonIgnore
    public Long getOwnerId() {return land.getOwnerId();}

    @JsonIgnore
    public boolean isAvailable() { return postStatus == PublicationStatus.AVAILABLE;}

    @JsonIgnore
    @Transient
    static public final Byte POST_NOT_SCORED = null;
}
