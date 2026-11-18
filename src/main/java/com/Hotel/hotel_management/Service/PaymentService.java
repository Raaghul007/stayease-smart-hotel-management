package com.Hotel.hotel_management.Service;




import com.Hotel.hotel_management.Dto.*;
import com.Hotel.hotel_management.Entity.*;
import com.Hotel.hotel_management.Repository.*;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRespository bookingRepository;

    public PaymentService(
            PaymentRepository paymentRepository,
            BookingRespository bookingRepository) {

        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
    }

    // Process simulated payment
    @Transactional
    public Payment makePayment(PaymentRequest request) {

        // Find booking
        Booking booking = bookingRepository
                .findById(request.getBookingId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Booking not found"));

        if ("CANCELLED".equals(booking.getBookingStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot pay for a cancelled booking");
        }

        // Check duplicate payment
        if (paymentRepository.existsByBookingId(booking.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Payment already exists for this booking");
        }

        // Validate payment method
        Set<String> methods = Set.of("CASH", "UPI", "CARD");

        String method = request.getPaymentMethod().toUpperCase();

        if (!methods.contains(method)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Payment method must be CASH, UPI or CARD");
        }

        // Create payment
        Payment payment = new Payment();

        payment.setBooking(booking);

        // Amount comes from database, not the user
        payment.setAmount(booking.getTotalAmount());

        payment.setPaymentMethod(method);

        // Academic project: simulate successful payment
        payment.setPaymentStatus("PAID");

        return paymentRepository.save(payment);
    }

    // Get all payments
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    // Get payment by ID
    public Payment getPaymentById(Long id) {

        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Payment not found"));
    }

    // Get payment by booking ID
    public Payment getPaymentByBookingId(Long bookingId) {

        return paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Payment not found for this booking"));
    }
}
