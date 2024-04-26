package com.masterproject.Master.Bob.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.masterproject.Master.Bob.model.JobCategory;
import com.masterproject.Master.Bob.model.User;
import com.masterproject.Master.Bob.repository.JobCategoryRepository;
import com.masterproject.Master.Bob.service.AdminService;
import com.masterproject.Master.Bob.service.RegistrationService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

@Controller
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;



    @GetMapping("/register")
    public String userRegistration(Model model){
        User user = new User();
        List<JobCategory> jobCategories = adminService.getAllJobCategories();

        model.addAttribute("user", user);
        model.addAttribute("jobCategories", jobCategories);

        return "registrationPage";
    }

    @PostMapping("/register")
    public String saveUser(@ModelAttribute User user, Model model, HttpServletRequest request) throws IOException {
        if(registrationService.checkUsername(user.getEmail()))
        {
            model.addAttribute("errorMessage", "This email has already exists!");
            return "registrationPage";
        }

        registrationService.register(user);
        String siteUrl = request.getRequestURL().toString().replace(request.getServletPath(), "");
        String verifyURL = siteUrl + "/verify?code=" + user.getVerificationCode();
        String text = "Dear " +  user.getName() + " " + user.getSurname() + ", "
                + "Please click the link below to verify your registration: "
                + verifyURL
                +" <br>Thank you, Master Bob Team";
        registrationService.sendEmail("mailtrap@demomailtrap.com",user.getEmail(),"Please verify your registration",
                text
                ,"Integration Test");
       // registrationService.sendVerificationEmail(user,siteURL);
        model.addAttribute("message", "You have successfully registered our app!");
        return "registerSuccess";
    }

    @GetMapping("/verify")
    public String verifyUser(@Param("code") String code) {
        return registrationService.verify(code) ? "verifySuccess" : "verifyFail";
    }
}
