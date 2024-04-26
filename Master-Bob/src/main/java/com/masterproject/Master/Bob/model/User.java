package com.masterproject.Master.Bob.model;

import jakarta.persistence.*;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String surname;
    @Column(name = "email")
    private String username;
    @Column(name = "phone_number")
    private String phoneNumber;
    private String password;

    private String address;

    private Double longitude;

    private Double latitude;

    @ManyToMany
    @JoinTable(
            name = "user_job_category", // Name of the join table
            joinColumns = @JoinColumn(name = "user_id"), // Foreign key column in the join table for user_id
            inverseJoinColumns = @JoinColumn(name = "category_id") // Foreign key column in the join table for category_id
    )
    private Set<JobCategory> jobCategories;

    @OneToMany(mappedBy = "customer")
    private List<ServiceRequest> customerServiceRequests;

    @OneToMany(mappedBy = "master")
    private List<ServiceRequest> masterServiceRequests;
    private String role;

    @Column(name = "post_code")
    private String postCode;

    public List<ServiceRequest> getCustomerServiceRequests() {
        return customerServiceRequests;
    }

    @Column(name = "verification_code", length = 64)
    private String verificationCode;

    private boolean enabled;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setEmail(String email) {
        this.username = username;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Set<JobCategory> getJobCategories() {
        return jobCategories;
    }

    public void setJobCategories(Set<JobCategory> jobCategories) {
        this.jobCategories = jobCategories;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public void setCustomerServiceRequests(List<ServiceRequest> customerServiceRequests) {
        this.customerServiceRequests = customerServiceRequests;
    }

    public List<ServiceRequest> getMasterServiceRequests() {
        return masterServiceRequests;
    }

    public void setMasterServiceRequests(List<ServiceRequest> masterServiceRequests) {
        this.masterServiceRequests = masterServiceRequests;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }
}
