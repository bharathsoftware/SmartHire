package com.smarthire.controller;

import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smarthire.dto.ApplicationResponse;
import com.smarthire.entity.Application;
import com.smarthire.entity.Job;
import com.smarthire.entity.User;
import com.smarthire.repository.JobRepository;
import com.smarthire.repository.UserRepository;
import com.smarthire.repository.JobSeekerProfileRepository;
import com.smarthire.repository.ResumeRepository;
import com.smarthire.entity.JobSeekerProfile;
import com.smarthire.entity.Resume;
import com.smarthire.service.ApplicationService;

@RestController
@RequestMapping("/api/recruiters")
@CrossOrigin(origins = "http://localhost:5173")
public class RecruiterApplicationController {

    private final ApplicationService applicationService;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final JobSeekerProfileRepository profileRepository;
    private final ResumeRepository resumeRepository;
    @Value("${smarthire.resume.storage-dir:./uploads/resumes}")
    private String resumeStorageDir;

    public RecruiterApplicationController(
            ApplicationService applicationService,
            JobRepository jobRepository,
            UserRepository userRepository,
            JobSeekerProfileRepository profileRepository,
            ResumeRepository resumeRepository) {

        this.applicationService = applicationService;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.resumeRepository = resumeRepository;
    }

    // ==========================================
    // GET APPLICATIONS FOR RECRUITER'S JOB
    // ==========================================

    @GetMapping("/jobs/{jobId}/applications")
    public ResponseEntity<?> getApplicationsForJob(
            @PathVariable Long jobId,
            Authentication authentication) {

        String email = authentication.getName();

        User recruiter = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Recruiter not found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new RuntimeException("Job not found"));

        // Check job ownership
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

    @GetMapping("/applications/{applicationId}/resume")
    public ResponseEntity<?> downloadResume(@PathVariable Long applicationId, Authentication authentication) {
        String email = authentication.getName();
        User recruiter = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Recruiter not found"));
        Application application = applicationService.getApplicationById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        if (application.getJob().getRecruiter() == null ||
                !application.getJob().getRecruiter().getId().equals(recruiter.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You cannot access this resume.");
        }
        Resume resume = resumeRepository.findByJobSeekerId(application.getJobSeeker().getId())
                .orElse(null);
        if (resume == null || resume.getFilePath() == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            Path file = Path.of(resume.getFilePath()).toAbsolutePath().normalize();
            Path allowedRoot = Path.of(resumeStorageDir).toAbsolutePath().normalize();
            if (!file.startsWith(allowedRoot) || !Files.isRegularFile(file)) {
                return ResponseEntity.notFound().build();
            }
            FileSystemResource resource = new FileSystemResource(file);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + file.getFileName().toString().replace("\"", "") + "\"")
                    .body(resource);
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body("Unable to read resume file.");
        }
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
                .jobSeekerName(application.getJobSeeker().getFullName())
                .jobSeekerEmail(application.getJobSeeker().getEmail())
                .degree(profileRepository.findByUserId(application.getJobSeeker().getId())
                        .map(JobSeekerProfile::getEducation).orElse("Not provided"))
                .resumeAvailable(resumeRepository.findByJobSeekerId(application.getJobSeeker().getId()).isPresent())
                .resumeFileName(resumeRepository.findByJobSeekerId(application.getJobSeeker().getId())
                        .map(Resume::getFileName).orElse(null))
                .status(application.getStatus())
                .appliedAt(application.getAppliedAt())
                .build();
    }
}