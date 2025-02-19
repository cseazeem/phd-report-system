package com.manuu.phdreport.database;

import com.manuu.phdreport.entity.Scholar;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;import java.util.Optional;

@RegisterBeanMapper(Scholar.class)
public interface ScholarDao {

@SqlUpdate("INSERT INTO phd_scholars (user_id, email, scholar_name, father_name, batch, roll_no, passing_year, " +
        "heading_date, enrollment_no, profile_photo_url, supervisor, co_supervisor, hod_nominee, " +
        "supervisor_nominee, research_title, title_status, status, phd_coordinator, nationality, " +
        "date_of_joining, viva_date, fellowship, full_time_or_part_time, year_of_admission, created_at, updated_at) " +  // Added year_of_admission
        "VALUES (:userId, :email, :scholarName, :fatherName, :batch, :rollNo, :passingYear, :headingDate, " +
        ":enrollmentNo, :profilePhotoUrl, :supervisor, :coSupervisor, :hodNominee, :supervisorNominee, " +
        ":researchTitle, :titleStatus, :status, :phdCoordinator, :nationality, :dateOfJoining, " +
        ":vivaDate, :fellowship, :fullTimeOrPartTime, :yearOfAdmission, current_timestamp, current_timestamp)")  // Added :yearOfAdmission
@GetGeneratedKeys
Long insertScholar(@BindBean Scholar scholar);



    @SqlQuery("SELECT * FROM phd_scholars WHERE id = :id")
    @RegisterBeanMapper(Scholar.class)
    Scholar findById(@Bind("id") Long id);


    @SqlUpdate("""
    UPDATE phd_scholars 
    SET scholar_name = :scholarName, father_name = :fatherName, email = :email,
        batch = :batch, roll_no = :rollNo, passing_year = :passingYear, heading_date = :headingDate,
        date_of_joining = :dateOfJoining, year_of_admission = :yearOfAdmission, enrollment_no = :enrollmentNo,
        profile_photo_url = :profilePhotoUrl, co_supervisor = :coSupervisor, phd_coordinator = :phdCoordinator,
        nationality = :nationality, viva_date = :vivaDate, fellowship = :fellowship, 
        full_time_or_part_time = :fullTimeOrPartTime, supervisor = :supervisor, hod_nominee = :hodNominee, 
        supervisor_nominee = :supervisorNominee, research_title = :researchTitle, 
        status = :status, title_status = :titleStatus
    WHERE id = :id
""")
    void update(@BindBean Scholar scholar);


}

