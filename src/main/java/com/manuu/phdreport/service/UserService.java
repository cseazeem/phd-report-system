package com.manuu.phdreport.service;

import com.manuu.phdreport.database.CoordinatorDao;
import com.manuu.phdreport.database.RACMemberDao;
import com.manuu.phdreport.database.ScholarDao;
import com.manuu.phdreport.database.UserDaoImpl;
import com.manuu.phdreport.entity.*;
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
    private final RACMemberDao racMemberDao;

    public Page<UserResponse> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageImpl<UserResponse> usersPage = userDao.findAll(pageable);

        List<UserResponse> userResponses = usersPage.getContent()
                .stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(userResponses, pageable, usersPage.getTotalElements());
    }

//    public Page<Scholar> getScholarUsers(int page, int size) {
//        Pageable pageable = PageRequest.of(page, size);
//        PageImpl<Scholar> usersPage = userDao.findAllByRole(pageable);
//
//        List<Scholar> userResponses = usersPage.getContent()
//                .stream()
//                .map(this::mapToUserResponse)
//                .collect(Collectors.toList());
//
//        return new PageImpl<>(userResponses, pageable, usersPage.getTotalElements());
//    }
public Page<Scholar> getScholarUsers(int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<Scholar> usersPage = userDao.findAllByRole(pageable); // Fetch paginated data

    // No need to map if there's no transformation required
    List<Scholar> userResponses = usersPage.getContent();

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

    public void approveUser(Long userId,String racMemberRole) throws UserNotFoundException {
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

        // If the user is a PhD Scholar, create an entry in `phd_scholars` table
        if ("SCHOLAR".equals(user.getRole())) {
            Scholar scholar = new Scholar();
            scholar.setUserId(user.getId());
            scholar.setEmail(user.getEmail());
            scholar.setScholarName("Default Name");  // Ensure scholar_name is not null
            scholar.setFatherName("Default Father Name");
            scholar.setBatch("Default Batch");
            scholar.setRollNo("Default Roll No");
            scholar.setPassingYear(null);
            scholar.setHeadingDate(null);
            scholar.setEnrollmentNo(null);
            scholar.setSupervisor("Default Supervisor");
            scholar.setHodNominee(null);
            scholar.setSupervisorNominee(null);
            scholar.setResearchTitle("Default Research Title");
            scholar.setStatus("APPROVED");
            scholar.setTitleStatus("Default Title Status");
            scholar.setPhdCoordinator(null);
            scholar.setYearOfAdmission(2000);
            scholar.setPhdCoordinator("Default PhD Coordinator");

            // Insert into phd_scholars table
            phdScholarDao.insertScholar(scholar);
            //send confirmation email to the user
            emailService.sendConfirmation(user.getEmail(), user.getRole());
        }

        if (user.getRole().equals("COORDINATOR")) {
            Coordinator coordinator = Coordinator.builder()
                    .userId(user.getId())
                    .email(user.getEmail())
                    .name("Default Name")  // Admin can update later
                    .department("Default Department")
                    .build();
            coordinatorDao.insertCoordinator(coordinator);
            emailService.sendConfirmation(user.getEmail(), "Coordinator Approved");
        }


        // Handle RAC_MEMBER role assignment
        if (user.getRole().equals("RAC_MEMBER")) {
//            if (!List.of("SUPERVISOR", "HOD_NOMINEE", "SUPERVISOR_NOMINEE").contains(racMemberRole)) {
//                throw new IllegalStateException("Invalid RAC_MEMBER role: " + racMemberRole);
//            }

            RACMember racMember = RACMember.builder()
                    .userId(user.getId())
                    .email(user.getEmail())
                    .role("SUPERVISOR") // Set role dynamically
                    .designation("Default Designation")
                    .name("Default Name")
                    .department("Default Department")
                    .build();

            racMemberDao.insertRACMember(racMember);
            emailService.sendConfirmation(user.getEmail(), "RAC Member Approved as " + racMemberRole);
        }

        //set the user's status to APPROVED
        user.setStatus("APPROVED");
        userDao.updateUserStatus(user.getId(),user.getStatus());  //save the updated user status to the database
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
