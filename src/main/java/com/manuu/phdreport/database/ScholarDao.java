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

    @SqlQuery("SELECT * FROM phd_scholars WHERE user_id = :id")
    @RegisterBeanMapper(Scholar.class)
    Scholar findByUserId(@Bind("id") Long id);


    @SqlUpdate("""
    UPDATE phd_scholars 
    SET 
        scholar_name = COALESCE(:scholarName, scholar_name), 
        father_name = COALESCE(:fatherName, father_name), 
        email = COALESCE(:email, email),
        batch = COALESCE(:batch, batch), 
        roll_no = COALESCE(:rollNo, roll_no), 
        passing_year = COALESCE(:passingYear, passing_year), 
        heading_date = COALESCE(:headingDate, heading_date),
        date_of_joining = COALESCE(:dateOfJoining, date_of_joining), 
        year_of_admission = COALESCE(:yearOfAdmission, year_of_admission), 
        enrollment_no = COALESCE(:enrollmentNo, enrollment_no),
        profile_photo_url = COALESCE(:profilePhotoUrl, profile_photo_url), 
        co_supervisor = COALESCE(:coSupervisor, co_supervisor), 
        phd_coordinator = COALESCE(:phdCoordinator, phd_coordinator),
        nationality = COALESCE(:nationality, nationality), 
        viva_date = COALESCE(:vivaDate, viva_date), 
        fellowship = COALESCE(:fellowship, fellowship), 
        full_time_or_part_time = COALESCE(:fullTimeOrPartTime, full_time_or_part_time), 
        supervisor = COALESCE(:supervisor, supervisor), 
        hod_nominee = COALESCE(:hodNominee, hod_nominee), 
        supervisor_nominee = COALESCE(:supervisorNominee, supervisor_nominee), 
        research_title = COALESCE(:researchTitle, research_title), 
        status = COALESCE(:status, status), 
        title_status = COALESCE(:titleStatus, title_status),
        updated_at = current_timestamp
    WHERE id = :id
""")
    void update(@BindBean Scholar scholar);

}

