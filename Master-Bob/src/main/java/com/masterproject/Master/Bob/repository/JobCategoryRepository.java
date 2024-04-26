package com.masterproject.Master.Bob.repository;

import com.masterproject.Master.Bob.model.JobCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobCategoryRepository extends JpaRepository<JobCategory, Integer> {
}
