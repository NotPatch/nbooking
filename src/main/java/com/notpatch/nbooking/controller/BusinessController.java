package com.notpatch.nbooking.controller;

import com.notpatch.nbooking.model.Business;
import com.notpatch.nbooking.service.BusinessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/businesses")
@RequiredArgsConstructor
public class BusinessController {

    private final BusinessService businessService;

    @GetMapping
    public List<Business> getAll() {
        return businessService.getAll();
    }

    @GetMapping("/{id}")
    public Business getById(@PathVariable Long id) {
        return businessService.findById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        businessService.delete(id);
        return ResponseEntity.noContent().build();
    }
}