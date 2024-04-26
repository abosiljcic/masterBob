package com.masterproject.Master.Bob.controller;

import com.masterproject.Master.Bob.model.ServiceRequest;
import com.masterproject.Master.Bob.model.User;
import com.masterproject.Master.Bob.service.CustomerService;
import com.masterproject.Master.Bob.service.MasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;

@Controller
public class MasterController {

    @Autowired
    MasterService masterService;


    @GetMapping("/service_request")
    public String getMasterServiceRequests (Model model)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> master = masterService.getUserByUsername(username);
        if (master.isEmpty())
        {
            model.addAttribute("errorMessage", "Master is not recognized!");
        }

        List<ServiceRequest> serviceRequestList = masterService.getAllMasterServiceRequests(master.get().getId());
        model.addAttribute("serviceRequestList",serviceRequestList);

        return "masterServiceRequests";
    }
}
