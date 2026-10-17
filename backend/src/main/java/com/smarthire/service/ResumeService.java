package com.smarthire.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.smarthire.entity.Resume;
import com.smarthire.repository.ResumeRepository;

@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;

    public ResumeService(ResumeRepository resumeRepository) {
        this.resumeRepository = resumeRepository;
    }

    public Resume saveResume(Resume resume) {

        resume.setUploadedAt(LocalDateTime.now());

        return resumeRepository.save(resume);
    }

    public Optional<Resume> getResumeByJobSeekerId(
            Long jobSeekerId) {

        return resumeRepository.findByJobSeekerId(jobSeekerId);
    }

    public Resume updateResume(
            Long jobSeekerId,
            Resume updatedResume) {

        Resume existingResume =
                resumeRepository.findByJobSeekerId(jobSeekerId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Resume not found"));

        existingResume.setFileName(
                updatedResume.getFileName());

        existingResume.setFileType(
                updatedResume.getFileType());

        existingResume.setFilePath(
                updatedResume.getFilePath());

        existingResume.setUploadedAt(
                LocalDateTime.now());

        return resumeRepository.save(existingResume);
    }

    public void deleteResume(Long jobSeekerId) {

        Resume resume =
                resumeRepository.findByJobSeekerId(jobSeekerId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Resume not found"));

        resumeRepository.delete(resume);
    }
}