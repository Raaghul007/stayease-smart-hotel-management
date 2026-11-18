package com.Hotel.hotel_management.Controller;



import com.Hotel.hotel_management.Dto.*;
import com.Hotel.hotel_management.Entity.*;
import com.Hotel.hotel_management.Service.*;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // Make payment
    @PostMapping
    public ResponseEntity<Payment> makePayment(
            @Valid @RequestBody PaymentRequest request) {

        return ResponseEntity.ok(
                paymentService.makePayment(request));
    }

    // Get all payments
    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {

        return ResponseEntity.ok(
                paymentService.getAllPayments());
    }

    // Get payment by ID
    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                paymentService.getPaymentById(id));
    }

    // Get payment by booking ID
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<Payment> getPaymentByBookingId(
            @PathVariable Long bookingId) {

        return ResponseEntity.ok(
                paymentService.getPaymentByBookingId(bookingId));
    }
}
