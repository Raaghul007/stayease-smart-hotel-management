package com.Hotel.hotel_management.Service;

import com.Hotel.hotel_management.Entity.Room;
import com.Hotel.hotel_management.Repository.*;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    // Add room
    public Room addRoom(Room room) {
        room.setId(null);
        return roomRepository.save(room);
    }
    // Get all rooms
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    // Get room by ID
    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Room not found"));
    }

    // Get available rooms
    public List<Room> getAvailableRooms() {
        return roomRepository.findByAvailableTrue();
    }

    // Get rooms by type
    public List<Room> getRoomsByType(String type) {
        return roomRepository.findByRoomTypeIgnoreCase(type);
    }

    // Update room
    public Room updateRoom(Long id, Room updatedRoom) {
        Room room = getRoomById(id);

        room.setRoomNumber(updatedRoom.getRoomNumber());
        room.setRoomType(updatedRoom.getRoomType());
        room.setPricePerNight(updatedRoom.getPricePerNight());
        room.setAvailable(updatedRoom.isAvailable());

        return roomRepository.save(room);
    }

    // Delete room
    public void deleteRoom(Long id) {
        Room room = getRoomById(id);
        roomRepository.delete(room);
    }
}


