package com.Hotel.hotel_management.Service;

import com.Hotel.hotel_management.Entity.Staff;
import com.Hotel.hotel_management.Repository.StaffRepository;

import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
public class StaffService {

    private final StaffRepository staffRepository;

    public StaffService(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    // Register new staff
    public Staff addStaff(Staff staff) {

        if (staff.getStaffName() == null ||
                staff.getStaffName().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Staff name is required");
        }

        if (staff.getEmail() == null ||
                staff.getEmail().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Email is required");
        }

        if (staffRepository.existsByEmail(staff.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email already registered");
        }

        if (staff.getPhone() == null ||
                !staff.getPhone().matches("^[0-9]{10}$")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Phone number must contain 10 digits");
        }

        if (staff.getSalary() == null ||
                staff.getSalary().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Salary must be greater than zero");
        }

        if (staff.getRole() == null ||
                !staff.getRole().equalsIgnoreCase("STAFF")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Role must be STAFF");
        }

        staff.setRole("STAFF");

        return staffRepository.save(staff);
    }

    // Get all staff
    public List<Staff> getAllStaff() {
        return staffRepository.findAll();
    }

    // Get staff by ID
    public Staff getStaffById(Long id) {
        return staffRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Staff not found"));
    }

    // Update staff
    public Staff updateStaff(Long id, Staff updatedStaff) {

        Staff staff = getStaffById(id);

        staff.setStaffName(updatedStaff.getStaffName());
        staff.setPhone(updatedStaff.getPhone());
        staff.setDesignation(updatedStaff.getDesignation());
        staff.setSalary(updatedStaff.getSalary());

        return staffRepository.save(staff);
    }

    // Delete staff
    public void deleteStaff(Long id) {

        Staff staff = getStaffById(id);

        staffRepository.delete(staff);
    }
}
