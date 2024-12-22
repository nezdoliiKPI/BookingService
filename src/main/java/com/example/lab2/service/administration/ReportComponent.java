package com.example.lab2.service.administration;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.lab2.entity.PublicationStatus;
import com.example.lab2.entity.Report;
import com.example.lab2.entity.post.Post;
import com.example.lab2.repository.PostRepository;
import com.example.lab2.repository.ReportRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReportComponent {
   private final ReportRepository reportRepository;

   private final PostRepository postRepository;

    public List<Report> getAll() {
        return reportRepository.findAll();
    }

    public Optional<Report> getReport(long id) {
        return reportRepository.findById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    @SuppressWarnings("unused")
    public Optional<Report> createPostReport(Report report) {
        Optional<Post> result = 
            postRepository.findByIdAndPostStatus(report.getPostId(), PublicationStatus.UNDER_REVIEW);

        if (result.isEmpty())
            return Optional.ofNullable(null);

        result.get().setPostStatus(PublicationStatus.DENIED);

        if (postRepository.save(result.get()) == null)
            throw new RuntimeException("Entity not saved");
        if (reportRepository.save(report) == null)
            throw new RuntimeException("Entity not saved");

        return Optional.of(report);
    }

    public boolean deleteReport(long id) {
        reportRepository.deleteById(id);
        return true;
    }

    public Optional<Report> getPostReport(Long postId) {
        return Optional.ofNullable(
            reportRepository.findByPostId(postId).stream()
                .max(Comparator.comparing(Report::getReportDate))
                .orElse(null)
        );
    }
}
