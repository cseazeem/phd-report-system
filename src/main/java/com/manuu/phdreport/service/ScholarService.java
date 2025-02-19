package com.manuu.phdreport.service;

import com.manuu.phdreport.database.ScholarDao;
import com.manuu.phdreport.database.UserDaoImpl;
import com.manuu.phdreport.entity.Scholar;
import com.manuu.phdreport.entity.User;
import com.manuu.phdreport.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScholarService {

    private final ScholarDao scholarDao;
    private final UserDaoImpl userDao;

    public void saveScholarDetails(Scholar request) {
        // Check if user exists
        User user = userDao.findByEmail(request.getEmail());
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        // Create and save PhD Scholar entry
        Scholar scholar = Scholar.builder()
                .userId(user.getId())
                .email(request.getEmail())
                .scholarName(request.getScholarName())
                .fatherName(request.getFatherName())
                .batch(request.getBatch())
                .rollNo(request.getRollNo())
                .passingYear(request.getPassingYear())
                .headingDate(request.getHeadingDate())
                .enrollmentNo(request.getEnrollmentNo()) // Optional
                .profilePhotoUrl(request.getProfilePhotoUrl()) // Optional
                .supervisor(request.getSupervisor())
                .coSupervisor(request.getCoSupervisor()) // Optional
                .hodNominee(request.getHodNominee()) // Optional
                .supervisorNominee(request.getSupervisorNominee()) // Optional
                .researchTitle(request.getResearchTitle())
                .titleStatus(request.getTitleStatus())
                .status(request.getStatus())
                .phdCoordinator(request.getPhdCoordinator()) // Optional
                .nationality(request.getNationality())
                .dateOfJoining(request.getDateOfJoining())
                .vivaDate(request.getVivaDate())
                .fellowship(request.getFellowship())
                .fullTimeOrPartTime(request.getFullTimeOrPartTime())
                .build();

        scholarDao.insertScholar(scholar);
    }

    public Scholar updateScholar(Long id, Scholar updatedScholar) {
        Scholar existingScholar = scholarDao.findById(id);
        if (existingScholar == null) {
            throw new IllegalArgumentException("Scholar not found");
        }

        // Updating fields
        existingScholar.setScholarName(updatedScholar.getScholarName());
        existingScholar.setFatherName(updatedScholar.getFatherName());
        existingScholar.setEmail(updatedScholar.getEmail());
        existingScholar.setBatch(updatedScholar.getBatch());
        existingScholar.setRollNo(updatedScholar.getRollNo());
        existingScholar.setPassingYear(updatedScholar.getPassingYear());
        existingScholar.setHeadingDate(updatedScholar.getHeadingDate());
        existingScholar.setDateOfJoining(updatedScholar.getDateOfJoining());
        existingScholar.setYearOfAdmission(updatedScholar.getYearOfAdmission());
        existingScholar.setEnrollmentNo(updatedScholar.getEnrollmentNo());
        existingScholar.setProfilePhotoUrl(updatedScholar.getProfilePhotoUrl());
        existingScholar.setCoSupervisor(updatedScholar.getCoSupervisor());
        existingScholar.setPhdCoordinator(updatedScholar.getPhdCoordinator());
        existingScholar.setNationality(updatedScholar.getNationality());
        existingScholar.setVivaDate(updatedScholar.getVivaDate());
        existingScholar.setFellowship(updatedScholar.getFellowship());
        existingScholar.setFullTimeOrPartTime(updatedScholar.getFullTimeOrPartTime());
        existingScholar.setSupervisor(updatedScholar.getSupervisor());
        existingScholar.setHodNominee(updatedScholar.getHodNominee());
        existingScholar.setSupervisorNominee(updatedScholar.getSupervisorNominee());
        existingScholar.setResearchTitle(updatedScholar.getResearchTitle());
        existingScholar.setStatus(updatedScholar.getStatus());
        existingScholar.setTitleStatus(updatedScholar.getTitleStatus());

        scholarDao.update(existingScholar);
        return existingScholar;
    }

}

