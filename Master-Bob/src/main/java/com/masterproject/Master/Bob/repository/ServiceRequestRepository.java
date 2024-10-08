package com.masterproject.Master.Bob.repository;

import com.masterproject.Master.Bob.model.ServiceRequest;
import com.masterproject.Master.Bob.model.ServiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

public interface ServiceRequestRepository extends JpaRepository<ServiceRequest,Integer> {

    @Query("SELECT COUNT(sr) = 0 FROM ServiceRequest sr WHERE sr.contractor.id = ?1 and NOT (dateTimeBegin > ?3 or dateTimeEnd < ?2) and NOT (id = ?4)")
    boolean getAvailableMasters (Integer id, Timestamp dateTimeBegin, Timestamp dateTimeEnd, Integer serviceRequestId);

    @Query("SELECT COUNT(sr) = 0 FROM ServiceRequest sr WHERE sr.contractor.id = ?1")
    boolean findMasterById (Integer id);

    @Query("SELECT sr FROM ServiceRequest sr WHERE sr.contractor.id = ?1")
    List<ServiceRequest> findAllContractorServiceRequests (Integer contractorId);

    @Query("SELECT sr FROM ServiceRequest sr WHERE sr.customer.id = ?1")
    List<ServiceRequest> findAllCustomerServiceRequests (Integer customerId);

    @Transactional
    @Modifying
    @Query("UPDATE ServiceRequest sr SET sr.additionalInfo = :additionalInfo, sr.serviceStatus = :status, sr.dateTimeBegin = :dateTimeBegin, sr.dateTimeEnd = :dateTimeEnd WHERE sr.id = :serviceRequestId")
    void editServiceRequest (String additionalInfo, ServiceStatus status, Timestamp dateTimeBegin, Timestamp dateTimeEnd, Integer serviceRequestId);
}
