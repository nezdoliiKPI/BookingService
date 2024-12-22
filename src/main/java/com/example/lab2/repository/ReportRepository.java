package com.example.lab2.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.lab2.entity.Report;
import java.util.List;


public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByPostId(Long postId);
}
