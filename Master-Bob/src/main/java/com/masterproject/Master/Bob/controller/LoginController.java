package com.masterproject.Master.Bob.controller;

import com.masterproject.Master.Bob.model.JobCategory;
import com.masterproject.Master.Bob.model.User;
import com.masterproject.Master.Bob.service.AdminService;
import com.masterproject.Master.Bob.service.LoginService;
import com.masterproject.Master.Bob.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class LoginController {
    @Autowired
    LoginService loginService;

    @Autowired
    AdminService adminService;

    @RequestMapping(value = {"/", "/user"}, method = RequestMethod.GET)
    public String homePage(Model model) {
        List<JobCategory> jobCategories = adminService.getAllJobCategories();
        model.addAttribute("jobCategories", jobCategories);

        return "homePage";
    }

  /*  @GetMapping("/user")
    public String loggedUser ()
    {
        return "homePage";
    }*/

    @GetMapping("/login")
    public String loginPage()
    {
        return "loginPage";
    }

    @PostMapping("/logout")
    public String logout()
    {
        return "redirect:/";
    }


}
