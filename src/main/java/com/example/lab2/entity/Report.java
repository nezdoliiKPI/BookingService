package com.example.lab2.entity;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reports")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Report {

    @Id
    @Builder.Default
    @Column(name = "report_id")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id = null;

    @JsonIgnore
    @Column(name = "author_id")
    private Long authorId;

    @Builder.Default
    @Nonnull
    @Column(name = "post_id")
    private Long postId = null;

    private String description;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Builder.Default
    @Column(name = "report_date")
    LocalDate reportDate = LocalDate.now();
}
