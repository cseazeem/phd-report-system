package com.manuu.phdreport.database;

import com.manuu.phdreport.entity.Report;
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

    @SqlUpdate("UPDATE reports SET status = 'PENDING', updated_at = current_timestamp WHERE id = :reportId")
    void updateReportStatus(@Bind("reportId") Long reportId, @Bind("status") String status);

    @SqlUpdate("UPDATE reports SET report_path = :signedPdfPath, updated_at = current_timestamp WHERE id = :reportId")
    void updateReportPath(@Bind("reportId") Long reportId, @Bind("signedPdfPath") String signedPdfPath);


}