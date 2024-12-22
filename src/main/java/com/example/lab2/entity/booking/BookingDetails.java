package com.example.lab2.entity.booking;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.example.lab2.entity.post.Post;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class BookingDetails {

    @Column(name = "post_id")
    private Long postId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    @NonNull private LocalDate endDate;

    @JsonIgnore
    public Long getDays() {
        return ChronoUnit.DAYS.between(this.startDate, this.endDate);
    }

    public boolean isDataConsistencyTo(Post post) {
        return startDate.compareTo(post.getMinDate()) >= 0 && endDate.compareTo(post.getMaxDate()) <= 0;
    }
}
