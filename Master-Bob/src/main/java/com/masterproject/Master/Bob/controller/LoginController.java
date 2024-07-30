package com.masterproject.Master.Bob.controller;

import com.masterproject.Master.Bob.model.JobCategory;
import com.masterproject.Master.Bob.model.User;
import com.masterproject.Master.Bob.service.AdminService;
import com.masterproject.Master.Bob.service.LoginService;
import com.masterproject.Master.Bob.service.RegistrationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
public class LoginController {
    @Autowired
    LoginService loginService;

    @Autowired
    AdminService adminService;

    @Autowired
    RegistrationService registrationService;

    @RequestMapping(value = {"/", "/user"}, method = RequestMethod.GET)
    public String homePage(Model model) {
        List<JobCategory> jobCategories = adminService.getAllJobCategories();
        model.addAttribute("jobCategories", jobCategories);

        return "homePage";
    }

    @GetMapping("/change-profile-info")
    public String changeProfileInfo (Model model)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Optional<User> user = loginService.getUserByUsername(username);
        if(user.isEmpty())
        {
            model.addAttribute("errorMessage","User is not recognized!");
            return "errorPage";
        }

        List<JobCategory> jobCategoriesList = loginService.getAllJobCategories();

        model.addAttribute("user", user.get());
        model.addAttribute("jobCategoriesList", jobCategoriesList);

        return "changeProfileInfo";
    }

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

    @PostMapping("/change-profile-info/{id}")
    public String saveProfileInfo (@ModelAttribute User user, @RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword, Model model) throws IOException {

      Optional<User> userDb = loginService.getUserById(user.getId());
      if (userDb.isEmpty())
      {
          model.addAttribute("errorMessage", "User was not found in the database.");
      }

      // selected job categories
      Set<JobCategory> jobCategories = user.getJobCategories();

      String userPassword = userDb.get().getPassword();

      if (!oldPassword.equals("")) {
          String password = loginService.updatePassword(userPassword, oldPassword, newPassword);
          if (password.equals("Incorrect old password")) {
              model.addAttribute("errorMessage", password);
              return changeProfileInfo(model);
          }

          user.setPassword(password);
      }
      else {
          user.setPassword(userPassword);
      }

        User updatedUser = loginService.changeProfileInfo(user,jobCategories);
        if (updatedUser == null)
        {
            model.addAttribute("errorMessage", "You cannot uncheck a category because you have service requests for that category.");
            return changeProfileInfo(model);
        }

        String text = "Dear " +  user.getName() + " " + user.getSurname() + ", "
                + "Your profile info has been changed. Best regards, Master Bob Team";
        registrationService.sendEmail("mailtrap@demomailtrap.com","anabos12300@gmail.com" /*user.getEmail()*/,"Changed profile info",
                text
                ,"Integration Test");

      model.addAttribute("message", "Successfully changed profile info!");

      return "homePage";
    }


}
