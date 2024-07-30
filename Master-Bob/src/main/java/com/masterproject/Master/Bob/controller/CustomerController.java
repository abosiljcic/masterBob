package com.masterproject.Master.Bob.controller;

import com.masterproject.Master.Bob.model.*;
import com.masterproject.Master.Bob.service.AdminService;
import com.masterproject.Master.Bob.service.CustomerService;
import com.masterproject.Master.Bob.service.RegistrationService;
import com.masterproject.Master.Bob.utility.DateTimeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/user/customer")
public class CustomerController {
    @Autowired
    CustomerService customerService;

    @GetMapping("/category/{id}")
    public String getCategory (@PathVariable("id") Integer id, Model model)
    {
        List<Job> jobs = customerService.getAllJobsByCategoryId(id);
        if(jobs.size() > 0){
            JobCategory category = jobs.get(0).getCategory();
            model.addAttribute("category",category);
        }

        model.addAttribute("jobs", jobs);

        return "category";
    }

    @GetMapping("/job-description/{id}")
    public String getJobDescription(@PathVariable("id") Integer id, Model model)
    {
        Optional<Job> job = customerService.getJobById(id);
        if(job.isEmpty())
        {
            model.addAttribute("errorMessage","Job is not recognized!");
            return "errorPage";
        }

        model.addAttribute("job",job.get());
        model.addAttribute("jobId", id); // used in js code

        return "jobDescription";
    }

    @GetMapping("/job/{id}")
    public String getJob (@PathVariable("id") Integer id, Model model)
    {
        Optional<Job> job = customerService.getJobById(id);
        if(job.isEmpty())
        {
            model.addAttribute("errorMessage","Job is not recognized!");
            return "errorPage";
        }

        ServiceRequest serviceRequest = new ServiceRequest();

        model.addAttribute("job",job.get());
        model.addAttribute("serviceRequest",serviceRequest);

        return "requestService";
    }

    @PostMapping("/service-request")
    public String saveServiceRequest (@ModelAttribute ServiceRequest serviceRequest, @RequestParam("jobId") Integer id, Model model) throws IOException
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Optional<User> user = customerService.getUserByUsername(username);
        if(user.isEmpty())
        {
            model.addAttribute("errorMessage","User is not recognized!");
            return "errorPage";
        }
        // Postavljanje user-a, customer_id
        serviceRequest.setCustomer(user.get());

        // Postavljanje statusa service request-a
        serviceRequest.setServiceStatus(ServiceStatus.PENDING);

        // Dohvatanje job-a
        Optional<Job> job = customerService.getJobById(id);
        if(job.isEmpty())
        {
            model.addAttribute("errorMessage","Job is not recognized!");
            return "errorPage";
        }
        // Postavljanje job-a, job_id
        serviceRequest.setJob(job.get());

        customerService.saveUserLongitudeLatitude(user.get());

        String message = customerService.saveServiceRequest(serviceRequest);

        if(message == null)
        {
            model.addAttribute("message","Successfully sent service request!");
        }
        else {
            model.addAttribute("message", message);
        }

        Integer categoryId = serviceRequest.getJob().getCategory().getId();

        /* Videti jos sta ovde vracati */
        return getCategory(categoryId,model);
    }

    @GetMapping("/service-request")
    public String getCustomerServiceRequests (Model model)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> customer = customerService.getUserByUsername(username);
        if (customer.isEmpty())
        {
            model.addAttribute("errorMessage", "Customer is not recognized!");
        }

        List<ServiceRequest> serviceRequestList = customerService.getAllCustomerServiceRequests(customer.get().getId());
        model.addAttribute("serviceRequestList",serviceRequestList);

        return "customerServiceRequests";
    }

    @RequestMapping("/service-request/delete/{id}")
    public String deleteServiceRequest(@PathVariable(name = "id") Integer serviceRequestId, Model model) {
        customerService.deleteServiceRequestById(serviceRequestId);
        model.addAttribute("message","Successfully deleted service request!");

        return getCustomerServiceRequests(model);
    }

    @RequestMapping("/service-request/edit/{id}")
    public String editServiceRequest (@PathVariable(name = "id") Integer serviceRequestId, Model model)
    {
        ServiceRequest serviceRequest = customerService.getServiceRequestById(serviceRequestId);
        model.addAttribute("serviceRequest", serviceRequest);

        model.addAttribute("serviceStatuses", ServiceStatus.values());

        return "editCustomerServiceRequest";
    }

    @PostMapping("/service-request/save/edit/{id}")
    public String saveEditedServiceRequest (@ModelAttribute ServiceRequest serviceRequest, Model model)
    {
        String message = customerService.editServiceRequest(serviceRequest);

        if(!message.equals(""))
        {
            model.addAttribute("errorMessage", message);
            return editServiceRequest(serviceRequest.getId(), model);
        }

        model.addAttribute("message", "Successfully edited service request!");

        return getCustomerServiceRequests(model);
    }

}

