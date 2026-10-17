package com.smarthire.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smarthire.dto.JobResponse;
import com.smarthire.entity.Job;
import com.smarthire.service.JobService;

@RestController
@RequestMapping("/api/job-seekers")
@CrossOrigin(origins = "http://localhost:5173")
public class JobSeekerController {

    private final JobService jobService;

    public JobSeekerController(JobService jobService) {
        this.jobService = jobService;
    }

    // Get all available jobs
    @GetMapping("/jobs")
    public ResponseEntity<List<JobResponse>> getAvailableJobs() {

        List<JobResponse> jobs = jobService.getAllJobs()
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(jobs);
    }

    // Search jobs by title
    @GetMapping("/jobs/search")
    public ResponseEntity<List<JobResponse>> searchJobs(
            @RequestParam String title) {

        List<JobResponse> jobs = jobService
                .searchByTitle(title)
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(jobs);
    }

    // Filter jobs by location
    @GetMapping("/jobs/location")
    public ResponseEntity<List<JobResponse>> jobsByLocation(
            @RequestParam String location) {

        List<JobResponse> jobs = jobService
                .searchByLocation(location)
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(jobs);
    }

    // Filter jobs by job type
    @GetMapping("/jobs/type")
    public ResponseEntity<List<JobResponse>> jobsByType(
            @RequestParam String jobType) {

        List<JobResponse> jobs = jobService
                .searchByJobType(jobType)
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(jobs);
    }

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
                .recruiterId(job.getRecruiter().getId())
                .recruiterName(job.getRecruiter().getFullName())
                .build();
    }
}