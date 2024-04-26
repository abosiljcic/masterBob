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



  /*  @Bean
    public RestTemplate restTemplate ()
    {
        return new RestTemplate();
    }*/

    /*@Autowired
    private JavaMailSender mailSender;*/

   /* @Bean
    public JavaMailSender mailSender ()
    {
        return new JavaMailSenderImpl();
    }*/

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

  /*  private String callNominatimAPI(String address)
    {
        String url = "https://nominatim.openstreetmap.org/search?q=" + address + "&format=json&addressdetails=1&countrycodes=RS";
        ResponseEntity<String> response = restTemplate().getForEntity(url, String.class);
        return response.getBody();
    }

    public double[][] parseJson (String json) throws IOException
    {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(json);

        double[][] coordinates = new double[rootNode.size()][2];
        for (int i = 0; i < rootNode.size(); i++) {
            JsonNode node = rootNode.get(i);
            coordinates[i][0] = node.get("lat").asDouble();
            coordinates[i][1] = node.get("lon").asDouble();
        }

        return coordinates;
    }*/

  /*  public void sendVerificationEmail(User user, String siteURL) throws MessagingException, UnsupportedEncodingException {
        String toAddress = user.getEmail();
        String fromAddress = "anabos123@gmail.com";
        String senderName = "Master Bob";
        String subject = "Please verify your registration";
        String content = "Dear [[name]],<br>"
                + "Please click the link below to verify your registration:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
                + "Thank you,<br>"
                + "Master Bob Team.";

        MimeMessage message = mailSender().createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(new InternetAddress(fromAddress));
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]", user.getName());
        String verifyURL = siteURL + "/verify?code=" + user.getVerificationCode();

        content = content.replace("[[URL]]", verifyURL);

        helper.setText(content, true);

        mailSender().send(message);

    }*/


}
