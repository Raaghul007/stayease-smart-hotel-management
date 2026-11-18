package com.Hotel.hotel_management.Repository;

import com.Hotel.hotel_management.Entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByAvailableTrue();

    List<Room> findByRoomTypeIgnoreCase(String roomType);
}

