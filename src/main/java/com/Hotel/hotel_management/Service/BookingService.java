package com.Hotel.hotel_management.Service;

import com.Hotel.hotel_management.Dto.*;
import com.Hotel.hotel_management.Entity.*;
import com.Hotel.hotel_management.Repository.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BookingService {

    private final BookingRespository bookingRepository;
    private final CustomerRepository customerRepository;
    private final RoomRepository roomRepository;

    public BookingService(
            BookingRespository bookingRepository,
            CustomerRepository customerRepository,
            RoomRepository roomRepository) {

        this.bookingRepository = bookingRepository;
        this.customerRepository = customerRepository;
        this.roomRepository = roomRepository;
    }

    // Create booking
    @Transactional
    public Booking createBooking(BookingRequest request) {

        LocalDate checkIn = request.getCheckInDate();
        LocalDate checkOut = request.getCheckOutDate();

        // Validate dates
        if (checkIn == null || checkOut == null ||
                !checkOut.isAfter(checkIn)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Check-out date must be after check-in date");
        }

        if (checkIn.isBefore(LocalDate.now())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Check-in date cannot be in the past");
        }

        // Find customer
        Customer customer = customerRepository
                .findById(request.getCustomerId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Customer not found"));

        // Find room
        Room room = roomRepository
                .findById(request.getRoomId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Room not found"));

        if (!room.isAvailable()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Room is currently unavailable for booking");
        }

        // Check existing booking
        boolean alreadyBooked =
                bookingRepository.existsOverlappingBooking(
                        room.getId(), checkIn, checkOut);

        if (alreadyBooked) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Room is already booked for these dates");
        }

        // Calculate total price
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);

        BigDecimal totalAmount = room.getPricePerNight()
                .multiply(BigDecimal.valueOf(nights));

        // Create booking
        Booking booking = new Booking();

        booking.setCustomer(customer);
        booking.setRoom(room);
        booking.setCheckInDate(checkIn);
        booking.setCheckOutDate(checkOut);
        booking.setTotalAmount(totalAmount);
        booking.setBookingStatus("CONFIRMED");

        return bookingRepository.save(booking);
    }

    // Get all bookings
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    // Get booking by ID
    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Booking not found"));
    }

    // Get customer bookings
    public List<Booking> getCustomerBookings(Long customerId) {
        return bookingRepository.findByCustomerId(customerId);
    }

    // Cancel booking
    @Transactional
    public Booking cancelBooking(Long id) {

        Booking booking = getBookingById(id);

        if ("CANCELLED".equals(booking.getBookingStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Booking already cancelled");
        }

        booking.setBookingStatus("CANCELLED");

        return bookingRepository.save(booking);
    }
}
