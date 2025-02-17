package com.manuu.phdreport.database;

import com.manuu.phdreport.entity.PhDScholarRequest;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

public interface PhDScholarDao {

    @SqlUpdate("INSERT INTO phd_scholars (user_id, email, heading_date, supervisor, student_name, batch, roll_no, passing_year, rac1_supervisor, co_supervisor, rac2_nominee, rac3_recommended, research_title, status, title_status, father_name, phd_coordinator, enroll_no, profile_photo_url, created_at, updated_at) " +
            "VALUES (:userId, :email, :headingDate, :supervisor, :studentName, :batch, :rollNo, :passingYear, :rac1Supervisor, :coSupervisor, :rac2Nominee, :rac3Recommended, :researchTitle, :status, :titleStatus, :fatherName, :phdCoordinator, :enrollNo, :profilePhotoUrl, current_timestamp, current_timestamp)")
    @GetGeneratedKeys
    Long insertScholar(@BindBean PhDScholarRequest scholar);

}

