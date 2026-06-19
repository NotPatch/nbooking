package com.notpatch.nbooking.controller;

import com.notpatch.nbooking.model.Reservation;
import com.notpatch.nbooking.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<Reservation> create(
            @RequestParam Long resourceId,
            @RequestParam Long customerId,
            @RequestBody @Valid Reservation reservation) {
        return ResponseEntity.status(201)
                .body(reservationService.create(resourceId, customerId, reservation));
    }

    @GetMapping("/resource/{resourceId}")
    public List<Reservation> getByResource(@PathVariable Long resourceId) {
        return reservationService.getByResource(resourceId);
    }

    @GetMapping("/{id}")
    public Reservation getById(@PathVariable Long id) {
        return reservationService.getById(id);
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        reservationService.cancel(id);
        return ResponseEntity.noContent().build();
    }
}