package com.quantum.controller;

import com.quantum.model.Reservation;
import com.quantum.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @Autowired
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/restaurants/{restaurantId}/tables/{tableId}")
    public ResponseEntity<Reservation> createReservation(
            @PathVariable UUID restaurantId,
            @PathVariable UUID tableId,
            @RequestBody Reservation reservation) {
        return ResponseEntity.ok(reservationService.createReservation(restaurantId, tableId, reservation));
    }

    @GetMapping("/restaurants/{restaurantId}")
    public ResponseEntity<List<Reservation>> getReservationsByRestaurant(@PathVariable UUID restaurantId) {
        return ResponseEntity.ok(reservationService.getReservationsByRestaurant(restaurantId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable UUID id) {
        return ResponseEntity.ok(reservationService.getReservationById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(
            @PathVariable UUID id,
            @RequestBody Reservation updatedReservation) {
        return ResponseEntity.ok(reservationService.updateReservation(id, updatedReservation));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable UUID id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }
}

