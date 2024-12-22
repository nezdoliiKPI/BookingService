package com.example.lab2.entity.post;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Embeddable
public class Address {

    @Column(name = "street_number", nullable = false)
    private String streetNumber;

    private String street; 

    private String city;

    @Column(name = "country_state", nullable = false)
    private String state;

    @Column(name = "postal_code", nullable = false)
    private String postalCode; 
}
