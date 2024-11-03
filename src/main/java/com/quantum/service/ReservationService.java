package com.quantum.service;

import com.quantum.model.Reservation;
import com.quantum.model.Restaurant;
import com.quantum.model.Table;
import com.quantum.repository.ReservationRepository;
import com.quantum.repository.RestaurantRepository;
import com.quantum.repository.TableRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final TableRepository tableRepository;
    private final RestaurantRepository restaurantRepository;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository,
                              TableRepository tableRepository,
                              RestaurantRepository restaurantRepository) {
        this.reservationRepository = reservationRepository;
        this.tableRepository = tableRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @Transactional
    public Reservation createReservation(UUID restaurantId, int tableId, Reservation reservation) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found with ID: " + restaurantId));
        Table table = tableRepository.findById(tableId)
                .orElseThrow(() -> new EntityNotFoundException("Table not found with ID: " + tableId));

        reservation.setRestaurant(restaurant);
        reservation.setTable(table);
        reservation.setStatus(Reservation.Status.PENDING);
        reservation.setCreatedAt(LocalDateTime.now());
        reservation.setUpdatedAt(LocalDateTime.now());
        return reservationRepository.save(reservation);
    }

    public List<Reservation> getReservationsByRestaurant(UUID restaurantId) {
        return reservationRepository.findByRestaurantId(restaurantId);
    }

    public Reservation getReservationById(UUID reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found with ID: " + reservationId));
    }

    @Transactional
    public Reservation updateReservation(UUID reservationId, Reservation updatedReservation) {
        Reservation existingReservation = getReservationById(reservationId);
        existingReservation.setCustomerName(updatedReservation.getCustomerName());
        existingReservation.setCustomerContact(updatedReservation.getCustomerContact());
        existingReservation.setReservationTime(updatedReservation.getReservationTime());
        existingReservation.setStatus(updatedReservation.getStatus());
        existingReservation.setUpdatedAt(LocalDateTime.now());
        return reservationRepository.save(existingReservation);
    }

    @Transactional
    public void deleteReservation(UUID reservationId) {
        reservationRepository.deleteById(reservationId);
    }
}
