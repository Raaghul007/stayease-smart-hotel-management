package com.Hotel.hotel_management.Controller;

import com.Hotel.hotel_management.Entity.Staff;
import com.Hotel.hotel_management.Service.StaffService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
@CrossOrigin(origins = "*")
public class StaffController {

    private final StaffService staffService;

    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    // Register staff
    @PostMapping
    public ResponseEntity<Staff> addStaff(
            @RequestBody Staff staff) {

        return ResponseEntity.ok(
                staffService.addStaff(staff));
    }

    // Get all staff
    @GetMapping
    public ResponseEntity<List<Staff>> getAllStaff() {

        return ResponseEntity.ok(
                staffService.getAllStaff());
    }

    // Get staff by ID
    @GetMapping("/{id}")
    public ResponseEntity<Staff> getStaffById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                staffService.getStaffById(id));
    }

    // Update staff
    @PutMapping("/{id}")
    public ResponseEntity<Staff> updateStaff(
            @PathVariable Long id,
            @RequestBody Staff staff) {

        return ResponseEntity.ok(
                staffService.updateStaff(id, staff));
    }

    // Delete staff
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStaff(
            @PathVariable Long id) {

        staffService.deleteStaff(id);

        return ResponseEntity.ok(
                "Staff deleted successfully");
    }
}
