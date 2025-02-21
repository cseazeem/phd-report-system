package com.manuu.phdreport.database;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

public interface ReportApprovalDao {

    @SqlUpdate("INSERT INTO report_approvals (report_id, rac_member_id, role, status, remarks, created_at) " +
            "VALUES (:reportId, :racMemberId, :role, :status, :remarks, current_timestamp)")
    @GetGeneratedKeys
    Long saveApproval(@Bind("reportId") Long reportId,
                      @Bind("racMemberId") Long racMemberId,
                      @Bind("role") String role,
                      @Bind("status") String status,
                      @Bind("remarks") String remarks);

    @SqlQuery("SELECT COUNT(*) FROM report_approvals WHERE report_id = :reportId AND status = 'APPROVED'")
    int countApprovedByRAC(@Bind("reportId") Long reportId);

    default boolean isReportFullyApproved(Long reportId) {
        return countApprovedByRAC(reportId) >= 3;
    }

}
