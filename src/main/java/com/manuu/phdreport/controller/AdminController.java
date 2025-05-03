package com.manuu.phdreport.controller;

import com.manuu.phdreport.entity.Notice;
import com.manuu.phdreport.entity.UserResponse;
import com.manuu.phdreport.exceptions.UserNotFoundException;
import com.manuu.phdreport.service.JwtService;
import com.manuu.phdreport.service.NoticeSevice;
import com.manuu.phdreport.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin Controller", description = "APIs for managing users and notices by administrators")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminController {

    private final UserService userService;
    private final NoticeSevice noticeSevice;
    private final JwtService jwtService;

    @Operation(summary = "Approve a user", description = "Approves a user by ID and optionally assigns a RAC member role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User approved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "User not found or already approved/rejected",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid role provided",
                    content = @Content)
    })
    @PutMapping("/approve-user/{userId}")
    public ResponseEntity<String> approveUser(
            @Parameter(description = "ID of the user to approve", required = true) @PathVariable Long userId,
            @Parameter(description = "Optional RAC member role to assign") @RequestParam(required = false) String racMemberRole) {
        try {
            userService.approveUser(userId, racMemberRole);
            return ResponseEntity.ok("User approved successfully.");
        } catch (UserNotFoundException e) {
            throw new UserNotFoundException("User not found or already approved/rejected.");
        } catch (IllegalStateException e) {
            throw new IllegalStateException("Invalid role: " + e.getMessage());
        }
    }

    @Operation(summary = "Reject a user", description = "Rejects a user by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User rejected successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "User not found or already approved/rejected",
                    content = @Content)
    })
    @PutMapping("/reject-user/{userId}")
    public ResponseEntity<String> rejectUser(
            @Parameter(description = "ID of the user to reject", required = true) @PathVariable Long userId) {
        try {
            userService.rejectUser(userId);
            return ResponseEntity.ok("User rejected successfully.");
        } catch (UserNotFoundException e) {
            throw new UserNotFoundException("User not found or already approved/rejected.");
        }
    }

    @Operation(summary = "Get all users", description = "Retrieves a paginated list of all users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of users retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    })
    @GetMapping("/users")
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of users per page", example = "10") @RequestParam(defaultValue = "10") int size) {
        Page<UserResponse> users = userService.getAllUsers(page, size);
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Upload a notice", description = "Uploads a notice with a file, title, and description. Requires ADMIN, COORDINATOR, or RAC_MEMBER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notice uploaded successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "403", description = "Access denied due to insufficient role",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Failed to upload notice",
                    content = @Content)
    })
    @PostMapping(value = "/upload/notice", consumes = "multipart/form-data")
    public ResponseEntity<String> uploadNotice(
            @Parameter(description = "JWT token in Authorization header", required = true) @RequestHeader("Authorization") String token,
            @Parameter(description = "File to upload", required = true) @RequestParam("file") MultipartFile file,
            @Parameter(description = "Title of the notice", required = true) @RequestParam("title") String title,
            @Parameter(description = "Description of the notice", required = true) @RequestParam("description") String description) {
        try {
            Long uploadedById = jwtService.extractUserId(token);
            String role = jwtService.extractRole(token);

            if (!List.of("ADMIN", "COORDINATOR", "RAC_MEMBER").contains(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
            }

            noticeSevice.uploadNotice(uploadedById, role, file, title, description);
            return ResponseEntity.ok("Notice uploaded successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload notice.");
        }
    }

    @Operation(summary = "Get all notices", description = "Retrieves a paginated list of all notices.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of notices retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Notice.class)))
    })
    @GetMapping("/notice")
    public ResponseEntity<List<Notice>> getAllNotices(
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of notices per page", example = "10") @RequestParam(defaultValue = "10") int size) {
        List<Notice> notices = noticeSevice.getAllNotices(page, size);
        return ResponseEntity.ok(notices);
    }
}