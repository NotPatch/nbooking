package com.notpatch.nbooking.repository;

import com.notpatch.nbooking.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    List<Resource> findByBusinessId(Long businessId);
}