package com.smarthire.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smarthire.entity.Resume;
import com.smarthire.entity.User;
import com.smarthire.repository.UserRepository;
import com.smarthire.service.ResumeService;

@RestController
@RequestMapping("/api/job-seeker/resume")
@CrossOrigin(origins = "http://localhost:5173")
public class ResumeController {

    private final ResumeService resumeService;
    private final UserRepository userRepository;
    private final com.smarthire.repository.JobSeekerProfileRepository profileRepository;
    @Value("${smarthire.resume.storage-dir:./uploads/resumes}")
    private String resumeStorageDir;

    public ResumeController(
            ResumeService resumeService,
            UserRepository userRepository,
            com.smarthire.repository.JobSeekerProfileRepository profileRepository) {

        this.resumeService = resumeService;
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
    }

    // ==========================================
    // SAVE RESUME
    // ==========================================

    @PostMapping
    public ResponseEntity<?> saveResume(
            @RequestBody Resume resume,
            Authentication authentication) {

        String email = authentication.getName();

        User jobSeeker = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Job seeker not found"));

        if (resumeService
                .getResumeByJobSeekerId(jobSeeker.getId())
                .isPresent()) {

            return ResponseEntity
                    .badRequest()
                    .body("Resume already exists.");
        }

        resume.setJobSeeker(jobSeeker);

        Resume savedResume =
                resumeService.saveResume(resume);

        return ResponseEntity.ok(savedResume);
    }

    // Upload a real PDF resume to local disk; MySQL stores its metadata.
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadPdf(@RequestPart("file") MultipartFile file,
                                       @RequestPart("degree") String degree,
                                       Authentication authentication) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please choose a PDF resume.");
        }
        if (file.getSize() > 5L * 1024 * 1024) {
            return ResponseEntity.badRequest().body("PDF must be 5 MB or smaller.");
        }
        String contentType = file.getContentType();
        String originalName = file.getOriginalFilename() == null ? "resume.pdf" : file.getOriginalFilename();
        if (!originalName.toLowerCase().endsWith(".pdf") ||
                (contentType != null && !contentType.equalsIgnoreCase("application/pdf"))) {
            return ResponseEntity.badRequest().body("Only PDF resumes are allowed.");
        }
        String email = authentication.getName();
        User jobSeeker = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Job seeker not found"));
        try {
            Path root = Path.of(resumeStorageDir).toAbsolutePath().normalize();
            Files.createDirectories(root);
            String storedName = UUID.randomUUID() + ".pdf";
            Path target = root.resolve(storedName).normalize();
            if (!target.startsWith(root)) return ResponseEntity.badRequest().body("Invalid file name.");
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            Resume resume = resumeService.getResumeByJobSeekerId(jobSeeker.getId())
                    .orElseGet(Resume::new);
            resume.setJobSeeker(jobSeeker);
            resume.setFileName(Path.of(originalName).getFileName().toString());
            resume.setFileType("application/pdf");
            resume.setFilePath(target.toString());
            Resume saved = resumeService.saveResume(resume);

            var profile = profileRepository.findByUserId(jobSeeker.getId())
                    .orElseGet(() -> com.smarthire.entity.JobSeekerProfile.builder()
                            .user(jobSeeker).skills("Not provided").build());
            profile.setEducation(degree == null ? "" : degree.trim());
            profileRepository.save(profile);
            return ResponseEntity.ok(java.util.Map.of(
                    "id", saved.getId(), "fileName", saved.getFileName(),
                    "fileType", saved.getFileType(), "uploadedAt", saved.getUploadedAt()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Could not save resume. Check the server uploads folder permissions.");
        }
    }

    // ==========================================
    // GET MY RESUME
    // ==========================================

    @GetMapping
    public ResponseEntity<?> getMyResume(
            Authentication authentication) {

        String email = authentication.getName();

        User jobSeeker = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Job seeker not found"));

        return resumeService
                .getResumeByJobSeekerId(jobSeeker.getId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ==========================================
    // UPDATE MY RESUME
    // ==========================================

    @PutMapping
    public ResponseEntity<?> updateResume(
            @RequestBody Resume updatedResume,
            Authentication authentication) {

        String email = authentication.getName();

        User jobSeeker = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Job seeker not found"));

        Resume updated =
                resumeService.updateResume(
                        jobSeeker.getId(),
                        updatedResume);

        return ResponseEntity.ok(updated);
    }

    // ==========================================
    // DELETE MY RESUME
    // ==========================================

    @DeleteMapping
    public ResponseEntity<?> deleteResume(
            Authentication authentication) {

        String email = authentication.getName();

        User jobSeeker = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Job seeker not found"));

        resumeService.deleteResume(jobSeeker.getId());

        return ResponseEntity.noContent().build();
    }
}