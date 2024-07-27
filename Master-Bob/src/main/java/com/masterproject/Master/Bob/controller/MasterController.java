package com.masterproject.Master.Bob.controller;

import com.masterproject.Master.Bob.model.*;
import com.masterproject.Master.Bob.service.CustomerService;
import com.masterproject.Master.Bob.service.MasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class MasterController {

    @Autowired
    MasterService masterService;

    @GetMapping("/service-request")
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

    @RequestMapping("/service-request/delete/{id}")
    public String deleteServiceRequest(@PathVariable(name = "id") Integer serviceRequestId, Model model) {
        masterService.deleteServiceRequestById(serviceRequestId);
        model.addAttribute("message","Successfully deleted service request!");

        return getMasterServiceRequests(model);
    }

    @RequestMapping("/service-request/edit/{id}")
    public String editServiceRequest (@PathVariable(name = "id") Integer serviceRequestId, Model model)
    {
        ServiceRequest serviceRequest = masterService.getServiceRequestById(serviceRequestId);
        model.addAttribute("serviceRequest", serviceRequest);

        model.addAttribute("serviceStatuses", ServiceStatus.values());

        return "editServiceRequest";
    }
    @PostMapping("/service-request/save/edit/{id}")
    public String saveEditedServiceRequest (@ModelAttribute ServiceRequest serviceRequest, Model model)
    {
        String message = masterService.editServiceRequest(serviceRequest);

        if(!message.equals(""))
        {
            model.addAttribute("errorMessage", message);
            return editServiceRequest(serviceRequest.getId(), model);
        }

        model.addAttribute("message", "Successfully edited service request!");

        return getMasterServiceRequests(model);
    }
}
