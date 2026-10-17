package com.smarthire.dto;

import java.time.LocalDateTime;
import com.smarthire.entity.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter @Builder @AllArgsConstructor
public class ApplicationResponse {
    private Long id;
    private Long jobId;
    private String jobTitle;
    private String companyName;
    private Long jobSeekerId;
    private String jobSeekerName;
    private String jobSeekerEmail;
    private String degree;
    private boolean resumeAvailable;
    private String resumeFileName;
    private ApplicationStatus status;
    private LocalDateTime appliedAt;
}
