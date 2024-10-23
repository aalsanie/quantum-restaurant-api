package com.quantum.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.quantum.model.Reservation;
import com.quantum.model.Restaurant;
import com.quantum.model.Table;
import com.quantum.repository.ReservationRepository;
import com.quantum.repository.RestaurantRepository;
import com.quantum.repository.TableRepository;
import com.quantum.service.ReservationService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private TableRepository tableRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private ReservationService reservationService;

    private UUID restaurantId;
    private UUID tableId;
    private UUID reservationId;
    private Restaurant restaurant;
    private Table table;
    private Reservation reservation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        restaurantId = UUID.randomUUID();
        tableId = UUID.randomUUID();
        reservationId = UUID.randomUUID();

        restaurant = new Restaurant();
        restaurant.setId(restaurantId);

        table = new Table();
        table.setId(tableId);

        reservation = new Reservation();
        reservation.setId(reservationId);
        reservation.setRestaurant(restaurant);
        reservation.setTable(table);
        reservation.setCustomerName("John Doe");
        reservation.setCustomerContact("1234567890");
        reservation.setReservationTime(LocalDateTime.now().plusDays(1));
        reservation.setStatus(Reservation.Status.PENDING);
        reservation.setCreatedAt(LocalDateTime.now());
        reservation.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void createReservation_ShouldReturnSavedReservation() {
        // Arrange
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(tableRepository.findById(tableId)).thenReturn(Optional.of(table));
        when(reservationRepository.save(reservation)).thenReturn(reservation);

        // Act
        Reservation savedReservation = reservationService.createReservation(restaurantId, tableId, reservation);

        // Assert
        assertNotNull(savedReservation);
        assertEquals(reservationId, savedReservation.getId());
        assertEquals(restaurantId, savedReservation.getRestaurant().getId());
        assertEquals(tableId, savedReservation.getTable().getId());
        assertEquals(Reservation.Status.PENDING, savedReservation.getStatus());
        verify(restaurantRepository, times(1)).findById(restaurantId);
        verify(tableRepository, times(1)).findById(tableId);
        verify(reservationRepository, times(1)).save(reservation);
    }

    @Test
    void createReservation_ShouldThrowException_WhenRestaurantNotFound() {
        // Arrange
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            reservationService.createReservation(restaurantId, tableId, reservation);
        });

        assertEquals("Restaurant not found with ID: " + restaurantId, exception.getMessage());
        verify(restaurantRepository, times(1)).findById(restaurantId);
        verify(tableRepository, never()).findById(tableId);
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void createReservation_ShouldThrowException_WhenTableNotFound() {
        // Arrange
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(tableRepository.findById(tableId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            reservationService.createReservation(restaurantId, tableId, reservation);
        });

        assertEquals("Table not found with ID: " + tableId, exception.getMessage());
        verify(restaurantRepository, times(1)).findById(restaurantId);
        verify(tableRepository, times(1)).findById(tableId);
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void getReservationsByRestaurant_ShouldReturnReservationList() {
        // Arrange
        when(reservationRepository.findByRestaurantId(restaurantId)).thenReturn(List.of(reservation));

        // Act
        List<Reservation> reservations = reservationService.getReservationsByRestaurant(restaurantId);

        // Assert
        assertNotNull(reservations);
        assertEquals(1, reservations.size());
        assertEquals(reservationId, reservations.get(0).getId());
        verify(reservationRepository, times(1)).findByRestaurantId(restaurantId);
    }

    @Test
    void getReservationById_ShouldReturnReservation() {
        // Arrange
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

        // Act
        Reservation foundReservation = reservationService.getReservationById(reservationId);

        // Assert
        assertNotNull(foundReservation);
        assertEquals(reservationId, foundReservation.getId());
        verify(reservationRepository, times(1)).findById(reservationId);
    }

    @Test
    void getReservationById_ShouldThrowException_WhenReservationNotFound() {
        // Arrange
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            reservationService.getReservationById(reservationId);
        });

        assertEquals("Reservation not found with ID: " + reservationId, exception.getMessage());
        verify(reservationRepository, times(1)).findById(reservationId);
    }

    @Test
    void updateReservation_ShouldReturnUpdatedReservation() {
        // Arrange
        Reservation updatedReservation = new Reservation();
        updatedReservation.setCustomerName("Jane Doe");
        updatedReservation.setCustomerContact("0987654321");
        updatedReservation.setReservationTime(LocalDateTime.now().plusDays(2));
        updatedReservation.setStatus(Reservation.Status.CONFIRMED);

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(reservation)).thenReturn(reservation);

        // Act
        Reservation result = reservationService.updateReservation(reservationId, updatedReservation);

        // Assert
        assertNotNull(result);
        assertEquals("Jane Doe", result.getCustomerName());
        assertEquals("0987654321", result.getCustomerContact());
        assertEquals(Reservation.Status.CONFIRMED, result.getStatus());
        verify(reservationRepository, times(1)).findById(reservationId);
        verify(reservationRepository, times(1)).save(reservation);
    }

    @Test
    void deleteReservation_ShouldDeleteReservation_WhenExists() {
        // Arrange
        doNothing().when(reservationRepository).deleteById(reservationId);

        // Act
        reservationService.deleteReservation(reservationId);

        // Assert
        verify(reservationRepository, times(1)).deleteById(reservationId);
    }
}
