package com.Hotel.hotel_management.Repository;

import com.Hotel.hotel_management.Dto.*;
import com.Hotel.hotel_management.Entity.Booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookingRespository
        extends JpaRepository<Booking, Long> {

    // Find overlapping bookings for a room
    @Query("""
        SELECT COUNT(b) > 0
        FROM Booking b
        WHERE b.room.id = :roomId
        AND b.bookingStatus <> 'CANCELLED'
        AND b.checkInDate < :checkOut
        AND b.checkOutDate > :checkIn
    """)
    boolean existsOverlappingBooking(
            @Param("roomId") Long roomId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut
    );

    // Find bookings by customer
    List<Booking> findByCustomerId(Long customerId);

    // Find bookings by room
    List<Booking> findByRoomId(Long roomId);
}
