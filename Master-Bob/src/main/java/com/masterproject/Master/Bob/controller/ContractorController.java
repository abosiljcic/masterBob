package com.masterproject.Master.Bob.controller;

import com.masterproject.Master.Bob.model.*;
import com.masterproject.Master.Bob.service.ContractorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/user/contractor")
public class ContractorController {

    @Autowired
    ContractorService contractorService;

    @GetMapping("/service-request")
    public String getMasterServiceRequests (Model model)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> master = contractorService.getUserByUsername(username);
        if (master.isEmpty())
        {
            model.addAttribute("errorMessage", "Master is not recognized!");
        }

        List<ServiceRequest> serviceRequestList = contractorService.getAllMasterServiceRequests(master.get().getId());
        model.addAttribute("serviceRequestList",serviceRequestList);

        return "masterServiceRequests";
    }

    @GetMapping("/book-a-date")
    public String getBookADatePage (Model model) {
        ServiceRequest serviceRequest = new ServiceRequest();
        List<Job> jobs = contractorService.getAllJobs();

        model.addAttribute("serviceRequest",serviceRequest);
        model.addAttribute("jobList", jobs);

        return "bookDate";
    }

    @PostMapping("/book-a-date")
    public String bookADate(@ModelAttribute ServiceRequest serviceRequest, Model model)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Optional<User> master = contractorService.getUserByUsername(username);
        if(master.isEmpty())
        {
            model.addAttribute("errorMessage","User is not recognized!");
            return "errorPage";
        }
        serviceRequest.setMaster(master.get());

        // Kada majstor bukira datum, onda je id user-a 1
        User user = contractorService.getUserById(1).get();

        serviceRequest.setCustomer(user);
        // Postavljanje statusa service request-a
        serviceRequest.setServiceStatus(ServiceStatus.PENDING);

        String message = contractorService.saveServiceRequest(serviceRequest);
        if(message.equals("You are not available during that period!"))
        {
            model.addAttribute("errorMessage",message);
            return getBookADatePage(model);
        }

        model.addAttribute("message",message);

        return getMasterServiceRequests(model);
    }

    @RequestMapping("/service-request/delete/{id}")
    public String deleteServiceRequest(@PathVariable(name = "id") Integer serviceRequestId, Model model) {
        contractorService.deleteServiceRequestById(serviceRequestId);
        model.addAttribute("message","Successfully deleted service request!");

        return getMasterServiceRequests(model);
    }

    @RequestMapping("/service-request/edit/{id}")
    public String editServiceRequest (@PathVariable(name = "id") Integer serviceRequestId, Model model)
    {
        ServiceRequest serviceRequest = contractorService.getServiceRequestById(serviceRequestId);
        model.addAttribute("serviceRequest", serviceRequest);

        model.addAttribute("serviceStatuses", ServiceStatus.values());

        return "editMasterServiceRequest";
    }
    @PostMapping("/service-request/save/edit/{id}")
    public String saveEditedServiceRequest (@ModelAttribute ServiceRequest serviceRequest, Model model)
    {
        String message = contractorService.editServiceRequest(serviceRequest);

        if(!message.equals(""))
        {
            model.addAttribute("errorMessage", message);
            return editServiceRequest(serviceRequest.getId(), model);
        }

        model.addAttribute("message", "Successfully edited service request!");

        return getMasterServiceRequests(model);
    }
}
