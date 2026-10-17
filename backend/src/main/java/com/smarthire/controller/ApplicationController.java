package com.smarthire.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smarthire.dto.ApplicationResponse;
import com.smarthire.entity.Application;
import com.smarthire.entity.ApplicationStatus;
import com.smarthire.entity.Job;
import com.smarthire.entity.User;
import com.smarthire.repository.JobRepository;
import com.smarthire.repository.UserRepository;
import com.smarthire.service.ApplicationService;

@RestController
@RequestMapping("/api/applications")
@CrossOrigin(origins = "http://localhost:5173")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    public ApplicationController(
            ApplicationService applicationService,
            UserRepository userRepository,
            JobRepository jobRepository) {

        this.applicationService = applicationService;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
    }

    // ==========================================
    // GET ALL APPLICATIONS
    // RECRUITER CAN SEE ONLY THEIR APPLICATIONS
    // ==========================================

    @GetMapping
    public ResponseEntity<?> getAllApplications(
            Authentication authentication) {

        String email = authentication.getName();

        User recruiter = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Recruiter not found"));

        List<ApplicationResponse> applications =
                applicationService.getAllApplications()
                        .stream()
                        .filter(application ->
                                application.getJob()
                                        .getRecruiter()
                                        .getId()
                                        .equals(recruiter.getId())
                        )
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(applications);
    }

    // ==========================================
    // GET APPLICATION BY ID
    // RECRUITER CAN VIEW ONLY THEIR APPLICATION
    // ==========================================

    @GetMapping("/{id}")
    public ResponseEntity<?> getApplicationById(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();

        User recruiter = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Recruiter not found"));

        Application application =
                applicationService.getApplicationById(id)
                        .orElseThrow(() ->
                                new RuntimeException("Application not found"));

        Long applicationRecruiterId =
                application.getJob()
                        .getRecruiter()
                        .getId();

        if (!applicationRecruiterId.equals(recruiter.getId())) {

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("You can view only applications for your own jobs.");
        }

        return ResponseEntity.ok(
                toResponse(application)
        );
    }

    // ==========================================
    // GET MY APPLICATIONS
    // JOB SEEKER ONLY
    // ==========================================

    @GetMapping("/my-applications")
    public ResponseEntity<?> getMyApplications(
            Authentication authentication) {

        String email = authentication.getName();

        User jobSeeker = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Job seeker not found"));

        List<ApplicationResponse> applications =
                applicationService
                        .getApplicationsByJobSeeker(
                                jobSeeker.getId()
                        )
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(applications);
    }

    // ==========================================
    // GET APPLICATIONS FOR A JOB
    // RECRUITER CAN SEE ONLY THEIR JOB APPLICATIONS
    // ==========================================

    @GetMapping("/job/{jobId}")
    public ResponseEntity<?> getByJob(
            @PathVariable Long jobId,
            Authentication authentication) {

        String email = authentication.getName();

        User recruiter = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Recruiter not found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new RuntimeException("Job not found"));

        if (!job.getRecruiter().getId()
                .equals(recruiter.getId())) {

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("You can view applications only for your own jobs.");
        }

        List<ApplicationResponse> applications =
                applicationService
                        .getApplicationsByJob(jobId)
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(applications);
    }

    // ==========================================
    // UPDATE APPLICATION STATUS
    // RECRUITER CAN UPDATE ONLY THEIR APPLICATIONS
    // ==========================================

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestParam ApplicationStatus status,
            Authentication authentication) {

        String email = authentication.getName();

        User recruiter = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Recruiter not found"));

        Application application =
                applicationService.getApplicationById(id)
                        .orElseThrow(() ->
                                new RuntimeException("Application not found"));

        Long applicationRecruiterId =
                application.getJob()
                        .getRecruiter()
                        .getId();

        if (!applicationRecruiterId.equals(recruiter.getId())) {

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("You can update only applications for your own jobs.");
        }

        Application updatedApplication =
                applicationService.updateStatus(
                        id,
                        status
                );

        return ResponseEntity.ok(
                toResponse(updatedApplication)
        );
    }

    // ==========================================
    // DELETE APPLICATION
    // JOB SEEKER CAN DELETE ONLY THEIR OWN
    // ==========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteApplication(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();

        User jobSeeker = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Job seeker not found"));

        Application application =
                applicationService.getApplicationById(id)
                        .orElseThrow(() ->
                                new RuntimeException("Application not found"));

        if (!application.getJobSeeker().getId()
                .equals(jobSeeker.getId())) {

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("You can delete only your own application.");
        }

        applicationService.deleteApplication(id);

        return ResponseEntity.noContent().build();
    }

    // ==========================================
    // ENTITY → DTO
    // ==========================================

    private ApplicationResponse toResponse(
            Application application) {

        return ApplicationResponse.builder()
                .id(application.getId())
                .jobId(application.getJob().getId())
                .jobTitle(application.getJob().getTitle())
                .companyName(application.getJob().getCompanyName())
                .jobSeekerId(
                        application.getJobSeeker().getId()
                )
                .jobSeekerName(
                        application.getJobSeeker().getFullName()
                )
                .status(application.getStatus())
                .appliedAt(application.getAppliedAt())
                .build();
    }
}