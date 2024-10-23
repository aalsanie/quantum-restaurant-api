package com.quantum.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.quantum.model.Order;
import com.quantum.model.Payment;
import com.quantum.repository.OrderRepository;
import com.quantum.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private PaymentService paymentService;

    private UUID orderId;
    private UUID paymentId;
    private Order order;
    private Payment payment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orderId = UUID.randomUUID();
        paymentId = UUID.randomUUID();

        order = new Order();
        order.setId(orderId);
        order.setTotalAmount(100.0);
        order.setPaidAmount(50.0);

        payment = new Payment();
        payment.setId(paymentId);
        payment.setOrder(order);
        payment.setAmount(50.0);
        payment.setPaymentMethod(Payment.PaymentMethod.CARD);
        payment.setPaymentStatus(Payment.PaymentStatus.PENDING);
        payment.setPaymentDate(LocalDateTime.now());
    }

    @Test
    void createPayment_ShouldReturnSavedPayment_WhenPartialPayment() {
        // Arrange
        double partialPaymentAmount = 20.0; // Less than the remaining 50.0

        // Create a Payment object with the correct amount and status
        Payment partialPayment = new Payment();
        partialPayment.setId(UUID.randomUUID());
        partialPayment.setOrder(order);
        partialPayment.setAmount(partialPaymentAmount);
        partialPayment.setPaymentMethod(Payment.PaymentMethod.CARD);
        partialPayment.setPaymentStatus(Payment.PaymentStatus.PENDING); // Explicitly set status here
        partialPayment.setPaymentDate(LocalDateTime.now());

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment paymentArg = invocation.getArgument(0);
            paymentArg.setId(partialPayment.getId());
            return paymentArg;
        });

        // Act
        Payment savedPayment = paymentService.createPayment(orderId, partialPaymentAmount, Payment.PaymentMethod.CARD);

        // Assert
        assertNotNull(savedPayment);
        assertEquals(partialPaymentAmount, savedPayment.getAmount());
        assertEquals(Payment.PaymentStatus.PENDING, savedPayment.getPaymentStatus());
        assertEquals(70.0, order.getPaidAmount()); // 50 + 20
        assertEquals(30.0, order.getRemainingAmount()); // Total 100 - Paid 70 = Remaining 30
        verify(orderRepository, times(1)).save(order);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }


    @Test
    void createPayment_ShouldThrowException_WhenPaymentAmountExceedsRemaining() {
        // Arrange
        double overPaymentAmount = 60.0; // Remaining is 50.0
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            paymentService.createPayment(orderId, overPaymentAmount, Payment.PaymentMethod.CARD);
        });

        assertEquals("Payment amount must be positive and not exceed the remaining balance.", exception.getMessage());
        verify(paymentRepository, never()).save(any(Payment.class));
    }
}
