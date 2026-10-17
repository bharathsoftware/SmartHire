package com.smarthire.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smarthire.entity.Job;

public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByLocationIgnoreCase(String location);

    List<Job> findByJobTypeIgnoreCase(String jobType);

    List<Job> findByTitleContainingIgnoreCase(String title);

    List<Job> findByRecruiterId(Long recruiterId);
}