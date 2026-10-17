package com.smarthire.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smarthire.entity.JobSeekerProfile;
import com.smarthire.entity.User;
import com.smarthire.repository.UserRepository;
import com.smarthire.service.JobSeekerProfileService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/job-seeker/profile")
@CrossOrigin(origins = "http://localhost:5173")
public class JobSeekerProfileController {

    private final JobSeekerProfileService profileService;
    private final UserRepository userRepository;

    public JobSeekerProfileController(
            JobSeekerProfileService profileService,
            UserRepository userRepository) {

        this.profileService = profileService;
        this.userRepository = userRepository;
    }

    // ==========================================
    // CREATE PROFILE
    // ==========================================

    @PostMapping
    public ResponseEntity<?> createProfile(
            @Valid @RequestBody JobSeekerProfile profile,
            Authentication authentication) {

        String email = authentication.getName();

        User jobSeeker = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Job seeker not found"));

        if (profileService
                .getProfileByUserId(jobSeeker.getId())
                .isPresent()) {

            return ResponseEntity
                    .badRequest()
                    .body("Job seeker profile already exists.");
        }

        profile.setUser(jobSeeker);

        JobSeekerProfile savedProfile =
                profileService.createProfile(profile);

        return ResponseEntity.ok(savedProfile);
    }

    // ==========================================
    // GET MY PROFILE
    // ==========================================

    @GetMapping
    public ResponseEntity<?> getMyProfile(
            Authentication authentication) {

        String email = authentication.getName();

        User jobSeeker = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Job seeker not found"));

        return profileService
                .getProfileByUserId(jobSeeker.getId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ==========================================
    // UPDATE MY PROFILE
    // ==========================================

    @PutMapping
    public ResponseEntity<?> updateProfile(
            @Valid @RequestBody JobSeekerProfile updatedProfile,
            Authentication authentication) {

        String email = authentication.getName();

        User jobSeeker = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Job seeker not found"));

        JobSeekerProfile updated =
                profileService.updateProfile(
                        jobSeeker.getId(),
                        updatedProfile
                );

        return ResponseEntity.ok(updated);
    }

    // ==========================================
    // DELETE MY PROFILE
    // ==========================================

    @DeleteMapping
    public ResponseEntity<?> deleteProfile(
            Authentication authentication) {

        String email = authentication.getName();

        User jobSeeker = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Job seeker not found"));

        profileService.deleteProfile(jobSeeker.getId());

        return ResponseEntity.noContent().build();
    }
}