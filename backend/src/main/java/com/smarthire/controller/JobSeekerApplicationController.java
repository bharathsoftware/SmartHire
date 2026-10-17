package com.smarthire.controller;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smarthire.dto.ApplicationResponse;
import com.smarthire.entity.Application;
import com.smarthire.entity.ApplicationStatus;
import com.smarthire.entity.Job;
import com.smarthire.entity.User;
import com.smarthire.repository.ApplicationRepository;
import com.smarthire.repository.JobRepository;
import com.smarthire.repository.UserRepository;

@RestController
@RequestMapping("/api/job-seekers")
@CrossOrigin(origins = "http://localhost:5173")
public class JobSeekerApplicationController {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public JobSeekerApplicationController(
            ApplicationRepository applicationRepository,
            JobRepository jobRepository,
            UserRepository userRepository) {

        this.applicationRepository = applicationRepository;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    // ==========================================
    // APPLY FOR JOB
    // ==========================================

    @PostMapping("/apply/{jobId}")
    public ResponseEntity<?> applyForJob(
            @PathVariable Long jobId,
            Authentication authentication) {

        // Get logged-in user's email from JWT
        String email = authentication.getName();

        // Find logged-in Job Seeker
        User jobSeeker = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Job seeker not found"));

        // Find job
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new RuntimeException("Job not found"));

        // Check duplicate application
        boolean alreadyApplied =
                applicationRepository.existsByJobIdAndJobSeekerId(
                        jobId,
                        jobSeeker.getId()
                );

        if (alreadyApplied) {
            return ResponseEntity
                    .badRequest()
                    .body("You have already applied for this job.");
        }

        // Create application
        Application application = Application.builder()
                .job(job)
                .jobSeeker(jobSeeker)
                .status(ApplicationStatus.APPLIED)
                .appliedAt(LocalDateTime.now())
                .build();

        // Save application
        Application savedApplication =
                applicationRepository.save(application);

        // Create response
        ApplicationResponse response =
                ApplicationResponse.builder()
                        .id(savedApplication.getId())
                        .jobId(job.getId())
                        .jobTitle(job.getTitle())
                        .companyName(job.getCompanyName())
                        .jobSeekerId(jobSeeker.getId())
                        .jobSeekerName(jobSeeker.getFullName())
                        .status(savedApplication.getStatus())
                        .appliedAt(savedApplication.getAppliedAt())
                        .build();

        return ResponseEntity.ok(response);
    }
}