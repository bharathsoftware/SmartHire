package com.smarthire.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.smarthire.entity.JobSeekerProfile;
import com.smarthire.repository.JobSeekerProfileRepository;

@Service
public class JobSeekerProfileService {

    private final JobSeekerProfileRepository profileRepository;

    public JobSeekerProfileService(
            JobSeekerProfileRepository profileRepository) {

        this.profileRepository = profileRepository;
    }

    public JobSeekerProfile createProfile(
            JobSeekerProfile profile) {

        return profileRepository.save(profile);
    }

    public Optional<JobSeekerProfile> getProfileByUserId(
            Long userId) {

        return profileRepository.findByUserId(userId);
    }

    public JobSeekerProfile updateProfile(
            Long userId,
            JobSeekerProfile updatedProfile) {

        JobSeekerProfile existingProfile =
                profileRepository.findByUserId(userId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Job seeker profile not found"));

        existingProfile.setSkills(updatedProfile.getSkills());
        existingProfile.setEducation(updatedProfile.getEducation());
        existingProfile.setExperience(updatedProfile.getExperience());
        existingProfile.setResumeUrl(updatedProfile.getResumeUrl());
        existingProfile.setLinkedinUrl(updatedProfile.getLinkedinUrl());
        existingProfile.setGithubUrl(updatedProfile.getGithubUrl());
        existingProfile.setPortfolioUrl(updatedProfile.getPortfolioUrl());
        existingProfile.setPreferredLocation(
                updatedProfile.getPreferredLocation());
        existingProfile.setExpectedSalary(
                updatedProfile.getExpectedSalary());

        return profileRepository.save(existingProfile);
    }

    public void deleteProfile(Long userId) {

        JobSeekerProfile profile =
                profileRepository.findByUserId(userId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Job seeker profile not found"));

        profileRepository.delete(profile);
    }
}