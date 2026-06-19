package com.notpatch.nbooking.service;

import com.notpatch.nbooking.model.Customer;
import com.notpatch.nbooking.model.Reservation;
import com.notpatch.nbooking.model.ReservationStatus;
import com.notpatch.nbooking.model.Resource;
import com.notpatch.nbooking.repository.CustomerRepository;
import com.notpatch.nbooking.repository.ReservationRepository;
import com.notpatch.nbooking.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ResourceRepository resourceRepository;
    private final CustomerRepository customerRepository;

    public Reservation create(Long resourceId, Long customerId, Reservation reservation) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("Resource" + resourceId + " not found"));

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer: " + customerId + " not found"));

        boolean conflict = reservationRepository.existsConflict(
                resourceId, reservation.getStartTime(), reservation.getEndTime()
        );
        if (conflict) {
            throw new RuntimeException("This reservation already exists");
        }

        reservation.setResource(resource);
        reservation.setCustomer(customer);
        reservation.setStatus(ReservationStatus.CONFIRMED);
        return reservationRepository.save(reservation);
    }

    public List<Reservation> getByResource(Long resourceId) {
        return reservationRepository.findByResourceId(resourceId);
    }

    public Reservation getById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation: " + id + " not found"));
    }

    public void cancel(Long id) {
        Reservation reservation = getById(id);
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
    }
}