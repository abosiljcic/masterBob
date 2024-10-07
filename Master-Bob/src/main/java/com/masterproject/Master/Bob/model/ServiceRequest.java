package com.masterproject.Master.Bob.model;

import jakarta.persistence.*;

import java.sql.Time;
import java.sql.Timestamp;

@Entity
@Table(name = "service_request")
public class ServiceRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;

    @ManyToOne
    @JoinColumn(name = "contractor_id")
    private User contractor;

    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ServiceStatus serviceStatus;

    @Column(name = "date_time_begin")
    private Timestamp dateTimeBegin;

    private String dateTimeBeginString;

    @Column(name = "date_time_end")
    private Timestamp dateTimeEnd;

    private String dateTimeEndString;
    @Column(name = "post_code")
    private String postCode;
    @Column(name = "additional_info")
    private String additionalInfo;

    @Column(name = "customer_address")
    private String customerAddress;

    public User getMaster() {
        return contractor;
    }

    public void setMaster(User contractor) {
        this.contractor = contractor;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public ServiceStatus getServiceStatus() {
        return serviceStatus;
    }

    public void setServiceStatus(ServiceStatus serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public Timestamp getDateTimeBegin() {
        return dateTimeBegin;
    }

    public void setDateTimeBegin(Timestamp dateTimeBegin) {
        this.dateTimeBegin = dateTimeBegin;
    }

    public String getDateTimeBeginString() {
        return dateTimeBeginString;
    }

    public void setDateTimeBeginString(String dateTimeBeginString) {
        this.dateTimeBeginString = dateTimeBeginString;
    }

    public Timestamp getDateTimeEnd() {
        return dateTimeEnd;
    }

    public void setDateTimeEnd(Timestamp dateTimeEnd) {
        this.dateTimeEnd = dateTimeEnd;
    }

    public String getDateTimeEndString() {
        return dateTimeEndString;
    }

    public void setDateTimeEndString(String dateTimeEndString) {
        this.dateTimeEndString = dateTimeEndString;
    }

}
