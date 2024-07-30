package com.masterproject.Master.Bob.service;


import com.masterproject.Master.Bob.configuration.CustomUserDetails;
import com.masterproject.Master.Bob.model.JobCategory;
import com.masterproject.Master.Bob.model.ServiceRequest;
import com.masterproject.Master.Bob.model.User;
import com.masterproject.Master.Bob.repository.JobCategoryRepository;
import com.masterproject.Master.Bob.repository.ServiceRequestRepository;
import com.masterproject.Master.Bob.repository.UserRepository;
import com.masterproject.Master.Bob.utility.NominatimAPI;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class LoginService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    JobCategoryRepository jobCategoryRepository;

    @Autowired
    ServiceRequestRepository serviceRequestRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            var userObj = user.get();
            return org.springframework.security.core.userdetails.User.builder()
                    .username(userObj.getEmail())
                    .password(userObj.getPassword())
                    .roles(userObj.getRole())
                    .build();
        } else {
            throw new UsernameNotFoundException(username);
        }
    }

    public Optional<User> getUserByUsername (String username)
    {
        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserById (Integer id)
    {
        return userRepository.findById(id);
    }

    public List<JobCategory> getAllJobCategories ()
    {
        return jobCategoryRepository.findAll();
    }

    public String updatePassword (String userPassword,String oldPassword, String newPassword)
    {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        //check old password
        if (!passwordEncoder.matches(oldPassword, userPassword)) {
            return "Incorrect old password";
        }

        // Update password
        return passwordEncoder.encode(newPassword);
    }

    public User changeProfileInfo(User user, Set<JobCategory> jobCategories) {

        // ako master prestane da se bavi necim proveriti da li u tom sektoru ima service request-ova
        if (user.getRole().equals("master") && checkMasterServices(user.getId(), jobCategories))
        {
            return null;
        }

        //setting longitude and latitude
        try {
            NominatimAPI nominatimAPI = new NominatimAPI();
            double[][] coordinates = nominatimAPI.parseJson(nominatimAPI.callNominatimAPI(user.getAddress(), user.getPostCode()));
            double latitude = 0.0;
            double longitude = 0.0;
            if (coordinates.length > 0) {
                latitude = coordinates[0][0];
                longitude = coordinates[0][1];
            }

            user.setLatitude(latitude);
            user.setLongitude(longitude);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return userRepository.save(user);
    }

    private boolean checkMasterServices (Integer masterId, Set<JobCategory> jobCategories)
    {
        List<ServiceRequest> serviceRequestList = serviceRequestRepository.findAllMasterServiceRequests(masterId);
        for (ServiceRequest sr : serviceRequestList)
        {
            if (jobCategories.contains(sr.getJob().getCategory()))
            {
                return false;
            }
        }

        return true;
    }

}