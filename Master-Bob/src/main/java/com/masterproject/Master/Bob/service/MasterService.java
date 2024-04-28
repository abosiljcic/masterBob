package com.masterproject.Master.Bob.service;

import com.masterproject.Master.Bob.model.ServiceRequest;
import com.masterproject.Master.Bob.model.ServiceStatus;
import com.masterproject.Master.Bob.model.User;
import com.masterproject.Master.Bob.repository.ServiceRequestRepository;
import com.masterproject.Master.Bob.repository.UserRepository;
import com.masterproject.Master.Bob.utility.DateTimeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        return serviceRequestRepository.findById(serviceRequestId).get();
    }

    public String editServiceRequest (Integer masterId,String additionalInfo, ServiceStatus status, String dateTimeBeginString, String dateTimeEndString, Integer serviceRequestId)
    {
        // Konverzija vremena u Timestamp
        DateTimeConverter dateTimeConverter = new DateTimeConverter();
        Timestamp dateTimeBegin = dateTimeConverter.convertToTimestamp(dateTimeBeginString);
        Timestamp dateTimeEnd = dateTimeConverter.convertToTimestamp(dateTimeEndString);

        // Provera da li je master dostupan u tom periodu
        if(!checkMastersAvailaility(masterId,dateTimeBegin,dateTimeEnd))
        {
            return "You are not available during that period!";
        }

        // Edit-ovanje
        serviceRequestRepository.editServiceRequest(additionalInfo, status, dateTimeBegin, dateTimeEnd, serviceRequestId);

        return "";
    }

    private boolean checkMastersAvailaility (Integer masterId, Timestamp dateTimeBegin, Timestamp dateTimeEnd)
    {
        return serviceRequestRepository.getAvailableMasters(masterId, dateTimeBegin, dateTimeEnd);
    }
}
