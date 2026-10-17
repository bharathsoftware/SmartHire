package com.smarthire.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter @Builder @AllArgsConstructor
public class RecruiterJobResponse {
    private Long id;
    private String title;
    private String companyName;
    private String location;
    private String jobType;
    private String experienceLevel;
    private String skills;
    private Double salary;
    private long applicantCount;
}
