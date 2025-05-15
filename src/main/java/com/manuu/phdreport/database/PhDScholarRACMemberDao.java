package com.manuu.phdreport.database;

import com.manuu.phdreport.entity.Notice;
import com.manuu.phdreport.entity.PhDScholarRACMember;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;

@RegisterBeanMapper(PhDScholarRACMember.class)
public interface PhDScholarRACMemberDao {

    @SqlUpdate("""
        INSERT INTO phd_scholar_rac_members (phd_scholar_id, rac_member_id, role)
        VALUES (:phdScholarId, :racMemberId, :role)
    """)
    @GetGeneratedKeys
    Long insert(@Bind("phdScholarId") Long scholarId,
                @Bind("racMemberId") Long racMemberId,
                @Bind("role") String role);

    @SqlQuery("""
        SELECT * FROM phd_scholar_rac_members
        WHERE phd_scholar_id = :phdScholarId
    """)
    List<PhDScholarRACMember> findByScholarId(@Bind("phdScholarId") Long scholarId);

    @SqlUpdate("""
        DELETE FROM phd_scholar_rac_members
        WHERE phd_scholar_id = :phdScholarId AND rac_member_id = :racMemberId
    """)
    void deleteByScholarAndRacMember(@Bind("phdScholarId") Long scholarId,
                                     @Bind("racMemberId") Long racMemberId);
}

