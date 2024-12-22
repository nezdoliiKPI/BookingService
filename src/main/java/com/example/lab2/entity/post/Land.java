package com.example.lab2.entity.post;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Embeddable
public class Land {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "owner_id") 
    private Long ownerId;

    @Nonnull 
    private String title;  

    @Nonnull
    @Column(length = 1000) 
    private String description;

    @Column(name = "total_area", nullable = false)
    private Integer totalArea;

    @Nonnull 
    @Embedded
    private Address address;

    @Column(name = "land_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private LandType type; 

    public enum LandType {
        HOUSE, APARTMENT, STUDIO, VILLA;
    }
}
