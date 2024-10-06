package com.masterproject.Master.Bob.service;

import com.masterproject.Master.Bob.model.*;
import com.masterproject.Master.Bob.repository.JobCategoryRepository;
import com.masterproject.Master.Bob.repository.JobRepository;
import com.masterproject.Master.Bob.repository.ServiceRequestRepository;
import com.masterproject.Master.Bob.repository.UserRepository;
import com.masterproject.Master.Bob.utility.DateTimeConverter;
import com.masterproject.Master.Bob.utility.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.origin.SystemEnvironmentOrigin;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class MasterService {

    @Autowired
    ServiceRequestRepository serviceRequestRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JobRepository jobRepository;

    @Autowired
    JobCategoryRepository jobCategoryRepository;

    public List<JobCategory> getAllJobCategories ()
    {
        return jobCategoryRepository.findAll();
    }
    public Optional<User> getUserById (Integer id)
    {
        return userRepository.findById(id);
    }
    public List<Job> getAllJobs ()
    {
        return jobRepository.findAll();
    }
    public List<ServiceRequest> getAllMasterServiceRequests (Integer masterId)
    {
        return serviceRequestRepository.findAllMasterServiceRequests(masterId);
    }
    public Optional<User> getUserByUsername (String username)
    {
        return userRepository.findByUsername(username);
    }

    public void deleteServiceRequestById (Integer serviceRequestId)
    {
        serviceRequestRepository.deleteById(serviceRequestId);
    }

    public ServiceRequest getServiceRequestById(Integer serviceRequestId)
    {
        Optional<ServiceRequest> serviceRequestOpt = serviceRequestRepository.findById(serviceRequestId);
        ServiceRequest serviceRequest = serviceRequestOpt.orElseThrow(() -> new NotFoundException("Service request not found with ID: " + serviceRequestId));

        if(serviceRequestOpt.isPresent())
        {
            if (serviceRequest.getDateTimeBegin() == null)
            {
                serviceRequest.setDateTimeBeginString("");
            }
            else
            {
                serviceRequest.setDateTimeBeginString(serviceRequest.getDateTimeBegin().toString());
            }

            if (serviceRequest.getDateTimeEnd() == null)
            {
                serviceRequest.setDateTimeEndString("");
            }
            else
            {
                serviceRequest.setDateTimeEndString(serviceRequest.getDateTimeEnd().toString());
            }
        }

        return  serviceRequest;

    }

    public String editServiceRequest (ServiceRequest serviceRequest)
    {
        // Konverzija vremena u Timestamp
        DateTimeConverter dateTimeConverter = new DateTimeConverter();
        Timestamp dateTimeBegin = dateTimeConverter.convertToTimestamp(serviceRequest.getDateTimeBeginString());
        Timestamp dateTimeEnd = dateTimeConverter.convertToTimestamp(serviceRequest.getDateTimeEndString());

        // Provera da li je master dostupan u tom periodu
        if(!checkMastersAvailability(serviceRequest.getMaster().getId(),dateTimeBegin,dateTimeEnd, serviceRequest.getId()))
        {
            return "You are not available during that period!";
        }

        // Edit-ovanje
        serviceRequestRepository.editServiceRequest(serviceRequest.getAdditionalInfo(), serviceRequest.getServiceStatus(), dateTimeBegin, dateTimeEnd, serviceRequest.getId());

        // Salje se mejl obavestenja customer-u da je service request koji je kreirao promenjen
        Email email = new Email();
        String text = "Dear " + serviceRequest.getCustomer().getName() + " " + serviceRequest.getCustomer().getSurname() +
                ", The service request " + serviceRequest.getId() + " you created has been changed. Please check your account. Best regards, Master Bob Team";
        try {
            email.sendEmail("mailtrap@demomailtrap.com","anabos12300@gmail.com","Edited service request " + serviceRequest.getId(),text,"");
        } catch (IOException e) {
            e.printStackTrace();
            return "Something went wrong while sending the email.";
        }

        return "";
    }

    public String saveServiceRequest(ServiceRequest serviceRequest)
    {
        System.out.println("Date time begin string: " + serviceRequest.getDateTimeBeginString());
        System.out.println("Date time end string: " + serviceRequest.getDateTimeEndString());

        DateTimeConverter dateTimeConverter = new DateTimeConverter();
        serviceRequest.setDateTimeBegin(dateTimeConverter.convertToTimestamp(serviceRequest.getDateTimeBeginString()));
        serviceRequest.setDateTimeEnd(dateTimeConverter.convertToTimestamp(serviceRequest.getDateTimeEndString()));

        System.out.println("Date time begin: " + serviceRequest.getDateTimeBegin());
        System.out.println("Date time end: " + serviceRequest.getDateTimeEnd());

        if(!checkMastersAvailability(serviceRequest.getMaster().getId(),serviceRequest.getDateTimeBegin(),serviceRequest.getDateTimeEnd(), -2))
        {
            return "You are not available during that period!";
        }

        serviceRequestRepository.save(serviceRequest);

        return "Date successfully booked!";
    }

    private boolean checkMastersAvailability (Integer masterId, Timestamp dateTimeBegin, Timestamp dateTimeEnd, Integer serviceRequestId)
    {
        return serviceRequestRepository.getAvailableMasters(masterId, dateTimeBegin, dateTimeEnd, serviceRequestId);
    }
}
