package com.smarthire.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smarthire.dto.RecruiterJobResponse;
import com.smarthire.entity.Job;
import com.smarthire.entity.User;
import com.smarthire.repository.UserRepository;
import com.smarthire.repository.ApplicationRepository;
import com.smarthire.service.RecruiterService;

@RestController
@RequestMapping("/api/recruiters")
@CrossOrigin(origins = "http://localhost:5173")
public class RecruiterController {

    private final RecruiterService recruiterService;
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;

    public RecruiterController(
            RecruiterService recruiterService,
            UserRepository userRepository,
            ApplicationRepository applicationRepository) {

        this.recruiterService = recruiterService;
        this.userRepository = userRepository;
        this.applicationRepository = applicationRepository;
    }

    // ==========================================
    // GET LOGGED-IN RECRUITER'S JOBS
    // ==========================================

    @GetMapping("/my-jobs")
    public ResponseEntity<List<RecruiterJobResponse>> getMyJobs(
            Authentication authentication) {

        String email = authentication.getName();

        User recruiter = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Recruiter not found"));

        List<RecruiterJobResponse> jobs =
                recruiterService
                        .getRecruiterJobs(recruiter.getId())
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(jobs);
    }

    // ==========================================
    // ENTITY → DTO
    // ==========================================

    private RecruiterJobResponse toResponse(Job job) {

        return RecruiterJobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .companyName(job.getCompanyName())
                .location(job.getLocation())
                .jobType(job.getJobType())
                .experienceLevel(job.getExperienceLevel())
                .skills(job.getSkills())
                .salary(job.getSalary())
                .applicantCount(applicationRepository.countForJob(job.getId()))
                .build();
    }
}