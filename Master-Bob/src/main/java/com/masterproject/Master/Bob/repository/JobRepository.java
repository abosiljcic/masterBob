package com.masterproject.Master.Bob.repository;

import com.masterproject.Master.Bob.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Integer> {
    @Query("SELECT j FROM Job j WHERE j.category.id = ?1")
    List<Job> getAllJobsByCategoryId (Integer id);
}
