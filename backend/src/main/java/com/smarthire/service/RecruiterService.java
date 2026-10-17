package com.smarthire.service;

import com.smarthire.entity.Job;
import com.smarthire.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecruiterService {

    private final JobRepository jobRepository;

    public RecruiterService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public List<Job> getRecruiterJobs(Long recruiterId) {
        return jobRepository.findByRecruiterId(recruiterId);
    }
}