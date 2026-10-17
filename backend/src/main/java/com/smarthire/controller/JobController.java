package com.smarthire.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smarthire.dto.JobResponse;
import com.smarthire.entity.Job;
import com.smarthire.entity.User;
import com.smarthire.repository.UserRepository;
import com.smarthire.service.JobService;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins = "http://localhost:5173")
public class JobController {

    private final JobService jobService;
    private final UserRepository userRepository;

    public JobController(
            JobService jobService,
            UserRepository userRepository) {

        this.jobService = jobService;
        this.userRepository = userRepository;
    }

    // ==========================================
    // CREATE JOB
    // Recruiter only
    // ==========================================

    @PostMapping
    public ResponseEntity<?> createJob(
            @RequestBody Job job,
            Authentication authentication) {

        String email = authentication.getName();

        User recruiter = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Recruiter not found"));

        // Always take recruiter from JWT
        job.setRecruiter(recruiter);

        Job savedJob = jobService.createJob(job);

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(savedJob));
    }

    // ==========================================
    // GET ALL JOBS
    // ==========================================

    @GetMapping
    public ResponseEntity<List<JobResponse>> getAllJobs() {

        List<JobResponse> jobs = jobService.getAllJobs()
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(jobs);
    }

    // ==========================================
    // GET JOB BY ID
    // ==========================================

    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> getJobById(
            @PathVariable Long id) {

        return jobService.getJobById(id)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ==========================================
    // SEARCH BY TITLE
    // ==========================================

    @GetMapping("/search/title")
    public ResponseEntity<List<JobResponse>> searchByTitle(
            @RequestParam String title) {

        return ResponseEntity.ok(
                jobService.searchByTitle(title)
                        .stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    // ==========================================
    // SEARCH BY LOCATION
    // ==========================================

    @GetMapping("/search/location")
    public ResponseEntity<List<JobResponse>> searchByLocation(
            @RequestParam String location) {

        return ResponseEntity.ok(
                jobService.searchByLocation(location)
                        .stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    // ==========================================
    // SEARCH BY JOB TYPE
    // ==========================================

    @GetMapping("/search/type")
    public ResponseEntity<List<JobResponse>> searchByJobType(
            @RequestParam String jobType) {

        return ResponseEntity.ok(
                jobService.searchByJobType(jobType)
                        .stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    // ==========================================
    // UPDATE JOB
    // Recruiter can update only their own job
    // ==========================================

    @PutMapping("/{id}")
    public ResponseEntity<?> updateJob(
            @PathVariable Long id,
            @RequestBody Job updatedJob,
            Authentication authentication) {

        String email = authentication.getName();

        User recruiter = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Recruiter not found"));

        Job existingJob = jobService.getJobById(id)
                .orElseThrow(() ->
                        new RuntimeException("Job not found"));

        // Check ownership
        if (!existingJob.getRecruiter().getId()
                .equals(recruiter.getId())) {

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("You can update only your own jobs.");
        }

        Job updated = jobService.updateJob(id, updatedJob);

        return ResponseEntity.ok(toResponse(updated));
    }

    // ==========================================
    // DELETE JOB
    // Recruiter can delete only their own job
    // ==========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJob(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();

        User recruiter = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Recruiter not found"));

        Job existingJob = jobService.getJobById(id)
                .orElseThrow(() ->
                        new RuntimeException("Job not found"));

        // Check ownership
        if (!existingJob.getRecruiter().getId()
                .equals(recruiter.getId())) {

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("You can delete only your own jobs.");
        }

        jobService.deleteJob(id);

        return ResponseEntity.noContent().build();
    }

    // ==========================================
    // ENTITY → DTO
    // ==========================================

    private JobResponse toResponse(Job job) {

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
                .recruiterId(
                        job.getRecruiter() != null
                                ? job.getRecruiter().getId()
                                : null
                )
                .recruiterName(
                        job.getRecruiter() != null
                                ? job.getRecruiter().getFullName()
                                : null
                )
                .build();
    }
}