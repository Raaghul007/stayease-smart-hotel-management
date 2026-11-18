package com.Hotel.hotel_management.Controller;


import com.Hotel.hotel_management.Entity.*;
import com.Hotel.hotel_management.Repository.*;
import com.Hotel.hotel_management.Service.RoomService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "*")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    // Add room
    @PostMapping
    public ResponseEntity<Room> addRoom(@Valid @RequestBody Room room) {
        return ResponseEntity.ok(roomService.addRoom(room));
    }

    // Get all rooms
    @GetMapping
    public ResponseEntity<List<Room>> getAllRooms() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    // Get room by ID
    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoomById(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.getRoomById(id));
    }

    // Get available rooms
    @GetMapping("/available")
    public ResponseEntity<List<Room>> getAvailableRooms() {
        return ResponseEntity.ok(roomService.getAvailableRooms());
    }

    // Get rooms by type
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Room>> getRoomsByType(
            @PathVariable String type) {
        return ResponseEntity.ok(roomService.getRoomsByType(type));
    }

    // Update room
    @PutMapping("/{id}")
    public ResponseEntity<Room> updateRoom(
            @PathVariable Long id,
            @Valid @RequestBody Room room) {
        return ResponseEntity.ok(roomService.updateRoom(id, room));
    }

    // Delete room
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.ok("Room deleted successfully");
    }
}

