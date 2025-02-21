package com.manuu.phdreport.database;

import com.manuu.phdreport.entity.Signature;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;

public interface SignatureDao {

    @SqlUpdate("INSERT INTO signatures (rac_member_id, report_id, signature_path, x, y, created_at) " +
            "VALUES (:racMemberId, :reportId, :signaturePath, :x, :y, current_timestamp)")
    void insertSignature(@BindBean Signature signature);

    @SqlQuery("SELECT * FROM signatures WHERE report_id = :reportId")
    @RegisterBeanMapper(Signature.class)
    List<Signature> getSignaturesForReport(@Bind("reportId") Long reportId);
}
