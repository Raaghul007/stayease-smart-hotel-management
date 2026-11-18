package com.Hotel.hotel_management.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.Hotel.hotel_management.Entity.Staff;

public interface StaffRepository
        extends JpaRepository<Staff, Long> {

    boolean existsByEmail(String email);
}
