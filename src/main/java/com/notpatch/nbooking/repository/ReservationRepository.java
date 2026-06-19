package com.notpatch.nbooking.repository;

import com.notpatch.nbooking.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByResourceId(Long resourceId);

    @Query("SELECT COUNT(r) > 0 FROM Reservation r WHERE r.resource.id = :resourceId " +
            "AND r.status != 'CANCELLED' " +
            "AND r.startTime < :endTime AND r.endTime > :startTime")
    boolean existsConflict(@Param("resourceId") Long resourceId,
                           @Param("startTime") LocalDateTime startTime,
                           @Param("endTime") LocalDateTime endTime);
}