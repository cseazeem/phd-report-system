package com.manuu.phdreport.database;

import com.manuu.phdreport.entity.Report;
import com.manuu.phdreport.entity.ReportDTO;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;

@RegisterBeanMapper(Report.class)
public interface ReportDao {

    @SqlUpdate("INSERT INTO reports (scholar_id, coordinator_id, status, report_path, created_at, updated_at) " +
            "VALUES (:scholarId, :coordinatorId, :status, :reportPath, current_timestamp, current_timestamp)")
    @GetGeneratedKeys
    Long insertReport(@BindBean Report report);

    @SqlQuery("SELECT * FROM reports WHERE scholar_id = :scholarId")
    Report findByScholarId(@Bind("scholarId") Long scholarId);

    @SqlQuery("SELECT * FROM reports WHERE coordinator_id = :coordinatorId")
    List<Report> findByCoordinatorId(@Bind("coordinatorId") Long coordinatorId);

    @SqlQuery("SELECT * FROM reports WHERE id = :id")
    Report findById(@Bind("id") Long id);

    @SqlUpdate("UPDATE reports SET status = :status, updated_at = current_timestamp WHERE id = :reportId")
    void updateReportStatus(@Bind("reportId") Long reportId, @Bind("status") String status);

    @SqlUpdate("UPDATE reports SET report_path = :signedPdfPath, updated_at = current_timestamp WHERE id = :reportId")
    void updateReportPath(@Bind("reportId") Long reportId, @Bind("signedPdfPath") String signedPdfPath);

//    @SqlQuery("select * from reports")
//    @RegisterBeanMapper(Report.class)
//    List<Report> findAll();


    @SqlQuery("""
    SELECT r.id, 
           r.scholar_id, 
           r.coordinator_id, 
           r.status AS report_status, 
           r.report_path, 
           ps.scholar_name, 
           ps.email, 
           ps.batch, 
           ps.roll_no, 
           ps.supervisor, 
           ps.research_title, 
           ps.status AS scholar_status
    FROM reports r
    LEFT JOIN phd_scholars ps ON r.scholar_id = ps.id
""")
    @RegisterBeanMapper(ReportDTO.class)
    List<ReportDTO> findAll();


    @SqlQuery("SELECT * FROM reports WHERE scholar_id = :id AND status = 'APPROVED'")
    @RegisterBeanMapper(Report.class)
    Report findApprovedReportById(@Bind("id") Long id);

    @SqlQuery("""
        SELECT r.* FROM reports r
        JOIN phd_scholar_rac_members srm ON r.scholar_id = srm.phd_scholar_id
        WHERE srm.rac_member_id = :racMemberId
        ORDER BY r.created_at DESC
    """)
    List<Report> findReportsByRacMemberId(@Bind("racMemberId") Long racMemberId);


}