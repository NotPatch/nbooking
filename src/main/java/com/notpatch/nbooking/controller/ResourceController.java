package com.notpatch.nbooking.controller;

import com.notpatch.nbooking.model.Resource;
import com.notpatch.nbooking.service.ResourceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/businesses/{businessId}/resources")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    @GetMapping
    public List<Resource> getAll(@PathVariable Long businessId) {
        return resourceService.getByBusiness(businessId);
    }

    @GetMapping("/{id}")
    public Resource getById(@PathVariable Long id) {
        return resourceService.getById(id);
    }

    @PostMapping
    public ResponseEntity<Resource> create(
            @PathVariable Long businessId,
            @RequestBody @Valid Resource resource) {
        return ResponseEntity.status(201).body(resourceService.create(businessId, resource));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        resourceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}