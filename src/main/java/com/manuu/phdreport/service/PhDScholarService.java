package com.manuu.phdreport.service;

import com.manuu.phdreport.database.PhDScholarDao;
import com.manuu.phdreport.database.UserDaoImpl;
import com.manuu.phdreport.entity.PhDScholarRequest;
import com.manuu.phdreport.entity.User;
import com.manuu.phdreport.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PhDScholarService {

    private final PhDScholarDao scholarDao;
    private final UserDaoImpl userDao;

    public void saveScholarDetails(PhDScholarRequest request) {
        // Check if user exists
        User user = userDao.findByEmail(request.getEmail());
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        // Create and save PhD Scholar entry
        PhDScholarRequest scholar = PhDScholarRequest.builder()
                .userId(user.getId())
                .email(request.getEmail())
                .headingDate(request.getHeadingDate())
                .supervisor(request.getSupervisor())
                .studentName(request.getStudentName())
                .batch(request.getBatch())
                .rollNo(request.getRollNo())
                .passingYear(request.getPassingYear())
                .fatherName(request.getFatherName())
                .researchTitle(request.getResearchTitle())
                .status(request.getStatus())
                .titleStatus(request.getTitleStatus())
                .phdCoordinator(request.getPhdCoordinator())  // Optional field
                .enrollNo(request.getEnrollNo())              // Optional field
                .profilePhotoUrl(request.getProfilePhotoUrl()) // Optional field
                .hodNominee(request.getHodNominee())          // Optional field
                .supervisorNominee(request.getSupervisorNominee()) // Optional field
                .build();

        scholarDao.insertScholar(scholar);
    }

//        // Update user status to APPROVED
//        userDao.updateUserStatus(request.getUserId(), "APPROVED");
    }

