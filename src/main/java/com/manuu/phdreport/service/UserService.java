package com.manuu.phdreport.service;

import com.manuu.phdreport.database.CoordinatorDao;
import com.manuu.phdreport.database.ScholarDao;
import com.manuu.phdreport.database.UserDaoImpl;
import com.manuu.phdreport.entity.Coordinator;
import com.manuu.phdreport.entity.Scholar;
import com.manuu.phdreport.entity.User;
import com.manuu.phdreport.entity.UserResponse;
import com.manuu.phdreport.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserDaoImpl userDao;
    private final EmailService emailService;
    private final ScholarDao phdScholarDao;
    private final CoordinatorDao coordinatorDao;

    public Page<UserResponse> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageImpl<UserResponse> usersPage = userDao.findAll(pageable);

        List<UserResponse> userResponses = usersPage.getContent()
                .stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(userResponses, pageable, usersPage.getTotalElements());
    }

    public Page<UserResponse> getScholarUsers(int page, int size, String role) {
        Pageable pageable = PageRequest.of(page, size);
        PageImpl<UserResponse> usersPage = userDao.findAllByRole(role, pageable);

        List<UserResponse> userResponses = usersPage.getContent()
                .stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(userResponses, pageable, usersPage.getTotalElements());
    }


    private UserResponse mapToUserResponse(UserResponse user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public void approveUser(Long userId) {
        //fetch the user from the database
        User user = Optional.ofNullable(userDao.findById(userId))
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        //check if the user has a PENDING status
        if (!user.getStatus().equals("PENDING")) {
            throw new UserNotFoundException("User is already approved or rejected.");
        }

        //check if the user has a valid role
        if (!List.of("SCHOLAR", "COORDINATOR", "RAC_MEMBER").contains(user.getRole())) {
            throw new IllegalStateException("Invalid role: " + user.getRole());
        }

        //set the user's status to APPROVED
        user.setStatus("APPROVED");
        userDao.updateUserStatus(user.getId(),user.getStatus());  //save the updated user status to the database

        // If the user is a PhD Scholar, create an entry in `phd_scholars` table
        if ("SCHOLAR".equals(user.getRole())) {
            Scholar scholar = new Scholar();
            scholar.setUserId(user.getId());
            scholar.setEmail(user.getEmail());
            scholar.setScholarName(null);  // Will be updated later
            scholar.setFatherName(null);
            scholar.setBatch(null);
            scholar.setRollNo(null);

            // Handle nullable integer field properly
            scholar.setPassingYear(null);  // Avoid Integer.parseInt("") error

            // Handle nullable date field properly
            scholar.setHeadingDate(null);  // Avoid LocalDate.parse("") error

            scholar.setEnrollmentNo(null);
            scholar.setSupervisor(null);
            scholar.setHodNominee(null);
            scholar.setSupervisorNominee(null);
            scholar.setResearchTitle(null);

            // Mandatory field with default value
            scholar.setStatus("APPROVED");

            scholar.setTitleStatus(null);
            scholar.setPhdCoordinator(null);

            // Insert into phd_scholars table
            phdScholarDao.insertScholar(scholar);
        }


        else if (user.getRole().equals("COORDINATOR")) {
            Coordinator coordinator = Coordinator.builder()
                    .userId(user.getId())
                    .name("Default Name")  // Admin can update later
                    .department("Default Department")
                    .build();
            coordinatorDao.insertCoordinator(coordinator);
            emailService.sendConfirmation(user.getEmail(), "Coordinator Approved");
        }

        //send confirmation email to the user
        emailService.sendConfirmation(user.getEmail(), user.getRole());
    }

    public void rejectUser(Long userId) {
        User user = Optional.ofNullable(userDao.findById(userId))
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        if (!user.getStatus().equals("PENDING")) {
            throw new UserNotFoundException("User has already been approved or rejected.");
        }
        user.setStatus("REJECTED");
        userDao.updateUserStatus(user.getId(),user.getStatus());
        emailService.sendRejectionNotification(user.getEmail());
    }
}
