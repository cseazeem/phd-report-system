package com.manuu.phdreport.controller;

import com.manuu.phdreport.entity.User;
import com.manuu.phdreport.entity.UserResponse;
import com.manuu.phdreport.exceptions.UserNotFoundException;
import com.manuu.phdreport.service.EmailService;
import com.manuu.phdreport.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @PutMapping("/approve-user/{userId}")
    public ResponseEntity<String> approveUser(@PathVariable Long userId,
                                              @RequestParam(required = false) String racMemberRole) {
        try {
            userService.approveUser(userId, racMemberRole);
            return ResponseEntity.ok("User approved successfully.");
        } catch (UserNotFoundException e) {
            throw new UserNotFoundException("User not found or already approved/rejected.");
        } catch (IllegalStateException e) {
            throw new IllegalStateException("Invalid role: " + e.getMessage());
        }
    }


    @PutMapping("/reject-user/{userId}")
    public ResponseEntity<String> rejectUser(@PathVariable Long userId) {
        try {
            userService.rejectUser(userId);
            return ResponseEntity.ok("User rejected successfully.");
        } catch (UserNotFoundException e) {
            throw new UserNotFoundException("User not found or already approved/rejected.");
        }
    }


    @GetMapping("/users")
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<UserResponse> users = userService.getAllUsers(page, size);
        return ResponseEntity.ok(users);
    }
}

