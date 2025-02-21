package com.manuu.phdreport.database;

import com.manuu.phdreport.entity.Report;
import com.manuu.phdreport.entity.User;
import lombok.RequiredArgsConstructor;
import org.jdbi.v3.core.Jdbi;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReportDaoImpl {

    private final Jdbi jdbi;

    private final ReportDao reportDao;


    public Long insertReport(Report report) {
        return jdbi.withExtension(ReportDao.class, dao -> dao.insertReport(report));
    }

    public Report findByScholarId(Long id) {
        return reportDao.findByScholarId(id);
    }

    public Report findById(Long id) {
        return reportDao.findById(id);
    }

    public void updateReportStatus(Long id, String status) {
        reportDao.updateReportStatus(id, status);
    }

    public void updateReportPath(Long reportId, String reportPath) {
        reportDao.updateReportPath(reportId, reportPath);
    }

}
