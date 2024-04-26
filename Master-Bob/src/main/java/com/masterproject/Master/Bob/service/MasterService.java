package com.masterproject.Master.Bob.service;

import com.masterproject.Master.Bob.model.ServiceRequest;
import com.masterproject.Master.Bob.model.User;
import com.masterproject.Master.Bob.repository.ServiceRequestRepository;
import com.masterproject.Master.Bob.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
