package com.masterproject.Master.Bob.controller;

import com.masterproject.Master.Bob.model.Job;
import com.masterproject.Master.Bob.model.JobCategory;
import com.masterproject.Master.Bob.model.User;
import com.masterproject.Master.Bob.service.AdminService;
import org.hibernate.boot.model.internal.JPAXMLOverriddenAnnotationReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user/admin")
public class AdminController {
    @Autowired
    AdminService adminService;

    @GetMapping("/getAllUsers")
    public String getAllUsers (Model model)
    {
        List<User> usersList = adminService.getAllUsers();
        model.addAttribute("usersList", usersList);
        return "manageUsers";
    }

    @GetMapping("/getAllJobs")
    public String getAllJobs (Model model)
    {
        Job job = new Job();
        model.addAttribute("job", job);

        JobCategory jobCategory = new JobCategory();
        model.addAttribute("jobCategory", jobCategory);

        List<Job> jobList = adminService.getAllJobs();
        model.addAttribute("jobList", jobList);

        List<JobCategory> jobCategories = adminService.getAllJobCategories();
        model.addAttribute("jobCategoryList",jobCategories);
        return "manageJobs";
    }

    @RequestMapping("/delete/{id}")
    public String deleteUser(@PathVariable(name = "id") Integer id, Model model) {
        try {
            adminService.deleteUser(id);
        }catch (Exception e)
        {
            e.printStackTrace();
            model.addAttribute("errorMessage", "You cannot delete this user because there are requests created/managed by that user.");
            return getAllUsers(model);
        }
        model.addAttribute("message","Successfully deleted user!");
        return getAllUsers(model);
    }

    @RequestMapping("/job/delete/{id}")
    public String deleteJob(@PathVariable(name="id") Integer id, Model model)
    {
        try {
            adminService.deleteJob(id);
        }catch (Exception e)
        {
            e.printStackTrace();
            model.addAttribute("errorMessage", "You cannot delete this job because there are requests for it.");
            return getAllJobs(model);
        }
        model.addAttribute("message","Successfully deleted job!");
        return getAllJobs(model);
    }
    @RequestMapping("/delete-category/{id}")
    public String deleteCategory(@PathVariable(name = "id") Integer id, Model model)
    {
        try
        {
            adminService.deleteJobCategory(id);
        }catch (Exception e)
        {
            e.printStackTrace();
            model.addAttribute("errorMessage", "You cannot delete this category because there are jobs in that category.");
            return getAllJobs(model);
        }

        model.addAttribute("message", "Successfully deleted category!");
        return getAllJobs(model);
    }

    @RequestMapping("/edit/{id}/{enabled}")
    public String editAccountEnablement(@PathVariable(name = "id") Integer id, @PathVariable("enabled") boolean enabled, Model model)
    {
       User user = adminService.getUser(id);

        user.setEnabled(!enabled);

        adminService.save(user);

        model.addAttribute("message", "Successfully changed account enablement of user " + user.getName() +
                " " + user.getSurname() + " to " + user.isEnabled());
        return getAllUsers(model);
    }
    @RequestMapping("/job/edit/{id}")
    public String editJob (@PathVariable(name = "id") Integer id, Model model)
    {
        Job job = adminService.getJobById(id);
        model.addAttribute("job", job);

        List<JobCategory> jobCategories = adminService.getAllJobCategories();
        model.addAttribute("jobCategoryList",jobCategories);

        return "editJob";
    }

    @PostMapping("/new-job")
    public String addNewJob (@ModelAttribute Job job, Model model)
    {
        if(job.getId() == null)
        {
            model.addAttribute("message", "Successfully added new job!");
        }
        else {
            model.addAttribute("message","Successfully edited new job!");
        }
        adminService.saveJob(job);


        return getAllJobs(model);
    }

    @PostMapping("new-job-category")
    public String addNewJobCategory (@ModelAttribute JobCategory jobCategory, Model model)
    {
        adminService.saveJobCategory(jobCategory);
        model.addAttribute("message", "Successfully added new job category!");

        return getAllJobs(model);
    }


}
