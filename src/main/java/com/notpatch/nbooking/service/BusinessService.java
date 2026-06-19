package com.notpatch.nbooking.service;

import com.notpatch.nbooking.model.Business;
import com.notpatch.nbooking.repository.BusinessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BusinessService {

    private final BusinessRepository businessRepository;

    public List<Business> getAll(){
        return businessRepository.findAll();
    }

    public Business findById(Long id){
        return businessRepository.findById(id).orElseThrow(() -> new RuntimeException("Business not found: " + id));
    }

    public void delete(Long id) {
        businessRepository.deleteById(id);
    }

}
