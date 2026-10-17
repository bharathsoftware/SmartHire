package com.smarthire.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smarthire.entity.Role;
import com.smarthire.repository.ApplicationRepository;
import com.smarthire.repository.JobRepository;
import com.smarthire.repository.UserRepository;

@RestController
@RequestMapping("/api/admin/dashboard")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminDashboardController {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;

    public AdminDashboardController(
            UserRepository userRepository,
            JobRepository jobRepository,
            ApplicationRepository applicationRepository) {

        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
        this.applicationRepository = applicationRepository;
    }

    // =========================
    // ADMIN DASHBOARD STATISTICS
    // =========================

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getDashboardStats(
            Authentication authentication) {

        long totalUsers = userRepository.count();

        long totalJobs = jobRepository.count();

        long totalApplications = applicationRepository.count();

        long totalJobSeekers =
                userRepository.findAll()
                        .stream()
                        .filter(user -> user.getRole() == Role.JOB_SEEKER)
                        .count();

        long totalRecruiters =
                userRepository.findAll()
                        .stream()
                        .filter(user -> user.getRole() == Role.RECRUITER)
                        .count();

        long totalAdmins =
                userRepository.findAll()
                        .stream()
                        .filter(user -> user.getRole() == Role.ADMIN)
                        .count();

        Map<String, Long> statistics = Map.of(
                "totalUsers", totalUsers,
                "totalJobs", totalJobs,
                "totalApplications", totalApplications,
                "totalJobSeekers", totalJobSeekers,
                "totalRecruiters", totalRecruiters,
                "totalAdmins", totalAdmins
        );

        return ResponseEntity.ok(statistics);
    }
}