package com.example.lab2.controller.specifications;

import java.time.LocalDate;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.example.lab2.entity.post.Land.LandType;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostFilter {

    private Float minCost = null;
    private Float maxCost = null;

    private Short minArea = null;
    private Short maxArea = null;

    private LandType type = null;

    private String city = null;

    private LocalDate minDate = null;
    private LocalDate maxDate = null;

    private String sortingType = null;

    @JsonIgnore
    private Long userId = null;

    public Pageable getPageable(int pageNumber, int pageSize) {
        if (sortingType == null)
            sortingType = "none";

        return switch (sortingType) {
            case "ascending" -> PageRequest.of(pageNumber, pageSize, Sort.by("costPerDay").ascending());
            case "descending"-> PageRequest.of(pageNumber, pageSize, Sort.by("costPerDay").descending());
            case "rate"-> PageRequest.of(pageNumber, pageSize, Sort.by("score").descending());
            default -> PageRequest.of(pageNumber, pageSize);
        };
    }
}
