package com.masterproject.Master.Bob.service;

import com.masterproject.Master.Bob.model.Job;
import com.masterproject.Master.Bob.model.JobCategory;
import com.masterproject.Master.Bob.model.ServiceRequest;
import com.masterproject.Master.Bob.model.User;
import com.masterproject.Master.Bob.repository.JobRepository;
import com.masterproject.Master.Bob.repository.ServiceRequestRepository;
import com.masterproject.Master.Bob.repository.UserRepository;
import com.masterproject.Master.Bob.utility.DateTimeConverter;
import com.masterproject.Master.Bob.utility.Email;
import com.masterproject.Master.Bob.utility.NominatimAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CustomerService {
    @Autowired
    JobRepository jobRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ServiceRequestRepository serviceRequestRepository;

    public List<Job> getAllJobsByCategoryId (Integer id)
    {
        return jobRepository.getAllJobsByCategoryId(id);
    }

    public Optional<Job> getJobById (Integer id)
    {
        return jobRepository.findById(id);
    }

    public Optional<User> getUserByUsername (String username)
    {
        return userRepository.findByUsername(username);
    }

    private Timestamp calculateDateTimeEnd (Optional<Job> job, ServiceRequest serviceRequest)
    {
        // Konverzija dateTimeBegin u Timestamp, odredjivanje dateTimeEnd
        Integer duration = job.get().getDuration() * 60 * 1000; // duration in milisec
        DateTimeConverter dateTimeConverter = new DateTimeConverter();
        serviceRequest.setDateTimeBegin(dateTimeConverter.convertToTimestamp(serviceRequest.getDateTimeBeginString()));
        long dateTimeEnd = serviceRequest.getDateTimeBegin().getTime() + duration;

        return new Timestamp(dateTimeEnd);
    }

    public String saveServiceRequest (ServiceRequest serviceRequest) throws IOException
    {
        Optional<Job> job = getJobById(serviceRequest.getJob().getId());
        if(job.isEmpty())
        {
            throw new IllegalStateException("Job is empty, cannot proceed.");
        }

        JobCategory jobCategory = job.get().getCategory();

        serviceRequest.setDateTimeEnd(calculateDateTimeEnd(job, serviceRequest));

        String message = null;
        // Ako nema registrovanih master-a za izabranu kategoriju, vrati odgovarajucu poruku
        Set<User> masters = jobCategory.getUsers();
        if (masters.size() == 0)
        {
            message = "There are no registered masters for the selected category.";
            return message;
        }

        List<User> availableMasters = new ArrayList<>();
        for (User m : masters)
        {
            if(serviceRequestRepository.findMasterById(m.getId()) || serviceRequestRepository.getAvailableMasters(m.getId(), serviceRequest.getDateTimeBegin(), serviceRequest.getDateTimeEnd(), -2))
            {
                availableMasters.add(m);
            }
        }

        // ukoliko nema dostupnih master-a, onda sve master-e koji rade u toj job kategoriji stavljam u dostupne
        // user-u se onda dodeljuje master koji mu je najblizi

        if (availableMasters.size() == 0)
        {
            availableMasters.addAll(masters);
            message = "Successfully sent service request! There are no masters available in the period you entered. The date will most likely change.";
        }

        //trazi se najblizi master
        User theClosestMaster = getTheClosestMaster(availableMasters, serviceRequest.getCustomer().getLatitude(), serviceRequest.getCustomer().getLongitude());

        serviceRequest.setMaster(theClosestMaster);

        // cuva se service request
        serviceRequestRepository.save(serviceRequest);

        // Salje se mejl obavestenja master-u da ima novi service request
        Email email = new Email();
        String text = "Dear " + theClosestMaster.getName() + " " + theClosestMaster.getSurname() +
                ", You have new service request. Please check your account. Best regards, Master Bob Team";
        email.sendEmail("mailtrap@demomailtrap.com","anabos12300@gmail.com","New service request",text,"");

        return message;
    }

    //F-ja koja racuna udaljenost od dostupnih majstora do customer-a koji je podneo service_request
    private User getTheClosestMaster (List<User> availableMasters, Double customerLatitude, Double customerLongitude)
    {
        double minDistance = Double.MAX_VALUE;
        User theClosestMaster = availableMasters.get(0);

        for (User m : availableMasters)
        {
            System.out.println("the closest Master: " + theClosestMaster.getName());
            Double masterLongitude = m.getLongitude();
            Double masterLatitude = m.getLatitude();

            double distance = calculateDistance(customerLatitude, customerLongitude, masterLatitude, masterLongitude);
            System.out.println("Distance " + distance);
            if(distance < minDistance)
            {
                System.out.println("Min distance: " + minDistance);
                System.out.println("The closest master in if: " + m.getName());
                minDistance = distance;
                theClosestMaster = m;
            }
        }

        return theClosestMaster;
    }

    //  the Haversine formula
    private double calculateDistance (double customerLatitude, double customerLongitude, double masterLatitude, double masterLongitude)
    {
        double latDistance = Math.toRadians(customerLatitude - masterLatitude);
        double lonDistance = Math.toRadians(customerLongitude - masterLongitude);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(customerLatitude)) * Math.cos(Math.toRadians(masterLatitude))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // EARTH_RADIUS = 6371
        return 6371 * c;
    }
    public void saveUserLongitudeLatitude (User user)
    {
        //setting longitude and latitude
        try{
            NominatimAPI nominatimAPI = new NominatimAPI();
            double[][] coordinates = nominatimAPI.parseJson(nominatimAPI.callNominatimAPI(user.getAddress(), user.getPostCode()));
            double latitude = 0.0;
            double longitude = 0.0;
            if(coordinates.length > 0)
            {
                latitude = coordinates[0][0];
                longitude = coordinates[0][1];
            }

            user.setLatitude(latitude);
            user.setLongitude(longitude);
        }catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public List<ServiceRequest> getAllCustomerServiceRequests (Integer customerId)
    {
        return serviceRequestRepository.findAllCustomerServiceRequests(customerId);
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
        // if user change dateTimeBegin calculate new dateTimeEnd and check master`s availability
        Optional<Job> job = getJobById(serviceRequest.getJob().getId());
        if(job.isEmpty())
        {
            throw new IllegalStateException("Job is empty, cannot proceed.");
        }
        Timestamp dateTimeEnd = calculateDateTimeEnd(job, serviceRequest);
        //Timestamp dateTimeEnd = dateTimeConverter.convertToTimestamp(serviceRequest.getDateTimeEndString());

        // Provera da li je master dostupan u tom periodu
        if(!checkMastersAvailability(serviceRequest.getMaster().getId(),dateTimeBegin,dateTimeEnd, serviceRequest.getId()))
        {
            return "Master is not available during that period!";
        }

        // Edit-ovanje
        serviceRequestRepository.editServiceRequest(serviceRequest.getAdditionalInfo(), serviceRequest.getServiceStatus(), dateTimeBegin, dateTimeEnd, serviceRequest.getId());

        // Salje se mejl obavestenja master-u da je service request za koji je zaduzen promenjen
        Email email = new Email();
        String text = "Dear " + serviceRequest.getMaster().getName() + " " + serviceRequest.getMaster().getSurname() +
                ", The service request " + serviceRequest.getId() + " you are responsible for has been changed. Please check your account. Best regards, Master Bob Team";
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