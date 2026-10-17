package com.smarthire.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smarthire.entity.Resume;

public interface ResumeRepository
        extends JpaRepository<Resume, Long> {

    Optional<Resume> findByJobSeekerId(Long jobSeekerId);

    boolean existsByJobSeekerId(Long jobSeekerId);
}