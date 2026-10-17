package com.smarthire.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smarthire.dto.ApplicationResponse;
import com.smarthire.dto.JobResponse;
import com.smarthire.dto.UserResponse;
import com.smarthire.entity.Application;
import com.smarthire.entity.Job;
import com.smarthire.entity.User;
import com.smarthire.repository.ApplicationRepository;
import com.smarthire.repository.JobRepository;
import com.smarthire.repository.UserRepository;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminController {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;

    public AdminController(
            UserRepository userRepository,
            JobRepository jobRepository,
            ApplicationRepository applicationRepository) {

        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
        this.applicationRepository = applicationRepository;
    }

    // =========================
    // GET ALL USERS
    // =========================

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers(
            Authentication authentication) {

        List<UserResponse> users = userRepository.findAll()
                .stream()
                .map(this::toUserResponse)
                .toList();

        return ResponseEntity.ok(users);
    }

    // =========================
    // GET ALL JOBS
    // =========================

    @GetMapping("/jobs")
    public ResponseEntity<List<JobResponse>> getAllJobs(
            Authentication authentication) {

        List<JobResponse> jobs = jobRepository.findAll()
                .stream()
                .map(this::toJobResponse)
                .toList();

        return ResponseEntity.ok(jobs);
    }

    // =========================
    // GET ALL APPLICATIONS
    // =========================

    @GetMapping("/applications")
    public ResponseEntity<List<ApplicationResponse>> getAllApplications(
            Authentication authentication) {

        List<ApplicationResponse> applications =
                applicationRepository.findAllWithDetails()
                        .stream()
                        .map(this::toApplicationResponse)
                        .toList();

        return ResponseEntity.ok(applications);
    }

    // =========================
    // DELETE USER
    // =========================

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(
            @PathVariable Long id,
            Authentication authentication) {

        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.badRequest().body(
                java.util.Map.of(
                        "message",
                        "User deletion is disabled for safety. " +
                        "Users may have jobs, applications, profiles or resumes."
                )
        );
    }

    // =========================
    // DELETE JOB
    // =========================

    @DeleteMapping("/jobs/{id}")
    public ResponseEntity<?> deleteJob(
            @PathVariable Long id,
            Authentication authentication) {

        if (!jobRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.badRequest().body(
                java.util.Map.of(
                        "message",
                        "Job deletion is disabled for safety. " +
                        "The job may have existing applications."
                )
        );
    }

    // =========================
    // USER RESPONSE
    // =========================

    private UserResponse toUserResponse(User user) {

        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .phone(user.getPhone())
                .location(user.getLocation())
                .build();
    }

    // =========================
    // JOB RESPONSE
    // =========================

    private JobResponse toJobResponse(Job job) {

        return JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .companyName(job.getCompanyName())
                .location(job.getLocation())
                .jobType(job.getJobType())
                .experienceLevel(job.getExperienceLevel())
                .skills(job.getSkills())
                .salary(job.getSalary())
                .recruiterId(job.getRecruiter().getId())
                .recruiterName(job.getRecruiter().getFullName())
                .build();
    }

    // =========================
    // APPLICATION RESPONSE
    // =========================

    private ApplicationResponse toApplicationResponse(
            Application application) {

        return ApplicationResponse.builder()
                .id(application.getId())
                .jobId(application.getJob().getId())
                .jobTitle(application.getJob().getTitle())
                .companyName(application.getJob().getCompanyName())
                .jobSeekerId(application.getJobSeeker().getId())
                .jobSeekerName(application.getJobSeeker().getFullName())
                .status(application.getStatus())
                .appliedAt(application.getAppliedAt())
                .build();
    }
}