package com.notpatch.nbooking.repository;

import com.notpatch.nbooking.model.Business;
import com.notpatch.nbooking.model.BusinessType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusinessRepository extends JpaRepository<Business, Long> {
    List<Business> findByBusinessType(BusinessType businessType);
    Optional<Business> findByEmail(String email);
}
