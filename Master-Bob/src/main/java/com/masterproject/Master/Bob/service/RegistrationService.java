package com.masterproject.Master.Bob.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.masterproject.Master.Bob.model.JobCategory;
import com.masterproject.Master.Bob.model.User;
import com.masterproject.Master.Bob.repository.JobCategoryRepository;
import com.masterproject.Master.Bob.repository.UserRepository;
import com.masterproject.Master.Bob.utility.Email;
import com.masterproject.Master.Bob.utility.NominatimAPI;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import net.bytebuddy.utility.RandomString;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

@Service
public class RegistrationService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JobCategoryRepository jobCategoryRepository;

    public boolean checkUsername(String email) {
        return userRepository.findByUsername(email).isPresent();
    }

    public User register(User user) {
        //encoding password
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        //setting verification code
        String randomCode = RandomString.make(64);
        user.setVerificationCode(randomCode);
        user.setEnabled(false);

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

        //sendVerificationEmail(user, siteURL);
    }

    public void sendEmail(String from, String to, String subject, String text, String category) throws IOException {
            Email email = new Email();
            email.sendEmail(from,to,subject,text,category);
    }

    public boolean verify(String verificationCode) {
        User user = userRepository.findByVerificationCode(verificationCode);

        if (user == null || user.isEnabled()) {
            return false;
        } else {
            user.setVerificationCode(null);
            user.setEnabled(true);
            userRepository.save(user);

            return true;
        }

    }

}
