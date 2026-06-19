package com.notpatch.nbooking.service;

import com.notpatch.nbooking.model.Business;
import com.notpatch.nbooking.model.Resource;
import com.notpatch.nbooking.repository.BusinessRepository;
import com.notpatch.nbooking.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final BusinessRepository businessRepository;

    public Resource create(Long businessId, Resource resource) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new RuntimeException("İşletme bulunamadı: " + businessId));
        resource.setBusiness(business);
        return resourceRepository.save(resource);
    }

    public List<Resource> getByBusiness(Long businessId) {
        return resourceRepository.findByBusinessId(businessId);
    }

    public Resource getById(Long id) {
        return resourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource: " + id + " not found"));
    }

    public void delete(Long id) {
        resourceRepository.deleteById(id);
    }
}