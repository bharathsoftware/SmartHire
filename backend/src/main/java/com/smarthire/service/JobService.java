package com.smarthire.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.smarthire.entity.Job;
import com.smarthire.repository.JobRepository;

@Service
public class JobService {

    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    // Create a job
    public Job createJob(Job job) {
        return jobRepository.save(job);
    }

    // Get all jobs
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    // Get job by ID
    public Optional<Job> getJobById(Long id) {
        return jobRepository.findById(id);
    }

    // Search jobs by title
    public List<Job> searchByTitle(String title) {
        return jobRepository.findByTitleContainingIgnoreCase(title);
    }

    // Search jobs by location
    public List<Job> searchByLocation(String location) {
        return jobRepository.findByLocationIgnoreCase(location);
    }

    // Search jobs by job type
    public List<Job> searchByJobType(String jobType) {
        return jobRepository.findByJobTypeIgnoreCase(jobType);
    }

    // Update job
    public Job updateJob(Long id, Job updatedJob) {

        Job existingJob = jobRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Job not found"));

        existingJob.setTitle(updatedJob.getTitle());
        existingJob.setDescription(updatedJob.getDescription());
        existingJob.setCompanyName(updatedJob.getCompanyName());
        existingJob.setLocation(updatedJob.getLocation());
        existingJob.setJobType(updatedJob.getJobType());
        existingJob.setExperienceLevel(updatedJob.getExperienceLevel());
        existingJob.setSkills(updatedJob.getSkills());
        existingJob.setSalary(updatedJob.getSalary());

        return jobRepository.save(existingJob);
    }

    // Delete job
    public void deleteJob(Long id) {
        jobRepository.deleteById(id);
    }
}