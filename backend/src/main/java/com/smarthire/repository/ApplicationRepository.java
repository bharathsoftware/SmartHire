package com.smarthire.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smarthire.entity.Application;
import com.smarthire.entity.ApplicationStatus;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    @Query("""
        SELECT a FROM Application a
        JOIN FETCH a.job
        JOIN FETCH a.jobSeeker
        """)
    List<Application> findAllWithDetails();

    @Query("""
        SELECT a FROM Application a
        JOIN FETCH a.job
        JOIN FETCH a.jobSeeker
        WHERE a.jobSeeker.id = :jobSeekerId
        """)
    List<Application> findByJobSeekerId(Long jobSeekerId);

    @Query("""
        SELECT a FROM Application a
        JOIN FETCH a.job
        JOIN FETCH a.jobSeeker
        WHERE a.job.id = :jobId
        """)
    List<Application> findByJobId(Long jobId);

    @Query("SELECT COUNT(a) FROM Application a WHERE a.job.id = :jobId")
    long countForJob(@Param("jobId") Long jobId);

    List<Application> findByStatus(ApplicationStatus status);

    boolean existsByJobIdAndJobSeekerId(
            Long jobId,
            Long jobSeekerId
    );
}