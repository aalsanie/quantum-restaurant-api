package com.quantum.service;

import com.quantum.model.Order;
import com.quantum.model.Payment;
import com.quantum.repository.OrderRepository;
import com.quantum.repository.PaymentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public Payment createPayment(UUID orderId, double amount, Payment.PaymentMethod paymentMethod) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + orderId));

        // Check if the amount is valid for partial payment
        double remainingAmount = order.getRemainingAmount();
        if (amount < 0 || amount > remainingAmount) {
            throw new IllegalArgumentException("Payment amount must be positive and not exceed the remaining balance.");
        }

        // Create the new payment
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(amount);
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentStatus(Payment.PaymentStatus.PENDING);
        payment.setPaymentDate(LocalDateTime.now());
        Payment savedPayment = paymentRepository.save(payment);

        // Update the order's paid amount
        order.setPaidAmount(order.getPaidAmount() + amount);

        // Check if the order is fully paid
        if (order.getRemainingAmount() == 0) {
            order.setStatus(Order.Status.COMPLETED);
        }

        orderRepository.save(order);

        return savedPayment;
    }

    public Payment getPaymentById(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found with ID: " + paymentId));
    }

    public List<Payment> getPaymentsByOrder(UUID orderId) {
        return paymentRepository.findByOrderId(orderId);
    }
}
