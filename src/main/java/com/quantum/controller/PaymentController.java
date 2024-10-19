package com.quantum.controller;

import com.quantum.model.Payment;
import com.quantum.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/orders/{orderId}/split")
    public ResponseEntity<Payment> createSplitPayment(
            @PathVariable UUID orderId,
            @RequestParam double amount,
            @RequestParam Payment.PaymentMethod paymentMethod) {
        return ResponseEntity.ok(paymentService.createPayment(orderId, amount, paymentMethod));
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<List<Payment>> getPaymentsByOrder(@PathVariable UUID orderId) {
        return ResponseEntity.ok(paymentService.getPaymentsByOrder(orderId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }
}
