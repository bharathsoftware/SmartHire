package com.smarthire.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class JobResponse {

    private Long id;
    private String title;
    private String description;
    private String companyName;
    private String location;
    private String jobType;
    private String experienceLevel;
    private String skills;
    private Double salary;

    private Long recruiterId;
    private String recruiterName;
}