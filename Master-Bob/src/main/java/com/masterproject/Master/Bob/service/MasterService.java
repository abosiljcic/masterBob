package com.masterproject.Master.Bob.service;

import com.masterproject.Master.Bob.model.ServiceRequest;
import com.masterproject.Master.Bob.model.ServiceStatus;
import com.masterproject.Master.Bob.model.User;
import com.masterproject.Master.Bob.repository.ServiceRequestRepository;
import com.masterproject.Master.Bob.repository.UserRepository;
import com.masterproject.Master.Bob.utility.DateTimeConverter;
import com.masterproject.Master.Bob.utility.Email;
import org.springframework.beans.factory.annotation.Autowired;
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

    private boolean checkMastersAvailability (Integer masterId, Timestamp dateTimeBegin, Timestamp dateTimeEnd, Integer serviceRequestId)
    {
        return serviceRequestRepository.getAvailableMasters(masterId, dateTimeBegin, dateTimeEnd, serviceRequestId);
    }
}
