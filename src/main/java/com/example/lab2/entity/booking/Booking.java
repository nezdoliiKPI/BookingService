package com.example.lab2.entity.booking;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bookings")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Booking {

    @Id
    @Builder.Default
    @Column(name = "booking_id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id = null;

    @Embedded
    BookingDetails bookingDetails;

    @Column(name = "booking_date")
    private LocalDate bookingDate;

    @Column(name = "total_cost")
    private Float totalCost;

    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Builder.Default
    @Column(name = "booking_status")
    @Enumerated(EnumType.STRING)
    private BookingStatus status = BookingStatus.PROCESSED;

    public long getDays() {
        return bookingDetails.getDays();
    }

    public enum BookingStatus {
        PROCESSED,
        ACTIVE,
        CLOSED,
        CANCELED
    }
}
