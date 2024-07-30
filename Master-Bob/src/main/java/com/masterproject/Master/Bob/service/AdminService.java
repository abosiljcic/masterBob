package com.masterproject.Master.Bob.service;

import com.masterproject.Master.Bob.model.Job;
import com.masterproject.Master.Bob.model.JobCategory;
import com.masterproject.Master.Bob.model.User;
import com.masterproject.Master.Bob.repository.JobCategoryRepository;
import com.masterproject.Master.Bob.repository.JobRepository;
import com.masterproject.Master.Bob.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    JobRepository jobRepository;

    @Autowired
    JobCategoryRepository jobCategoryRepository;

    public List<User> getAllUsers ()
    {
        return userRepository.findAll();
    }

    public List<Job> getAllJobs ()
    {
        return jobRepository.findAll();
    }

    public List<JobCategory> getAllJobCategories ()
    {
        return jobCategoryRepository.findAll();
    }
    public User getUser(Integer id) {
        return userRepository.findById(id).get();
    }
    public Job getJobById (Integer id){
        return jobRepository.findById(id).get();
    }

    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }
    public void deleteJob(Integer id){jobRepository.deleteById(id);}
    public void save(User user) {
        userRepository.save(user);
    }
    public void saveJob (Job job) {jobRepository.save(job);}

    public void saveJobCategory(JobCategory jobCategory) {jobCategoryRepository.save(jobCategory);}

    public void deleteJobCategory(Integer id)
    {
        jobCategoryRepository.deleteById(id);
    }
}
