package com.manuu.phdreport.database;

import com.manuu.phdreport.entity.RACMember;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;

import java.util.List;
import java.util.Optional;

@RegisterBeanMapper(RACMember.class)
public interface RACMemberDao {

    // 🔹 Insert a new RAC Member
    @SqlUpdate("INSERT INTO rac_members (user_id, name, role, department, created_at, update_at) " +
            "VALUES (:userId, :name, :role, :department, current_timestamp, current_timestamp)")
    @GetGeneratedKeys
    Long insertRACMember(@BindBean RACMember racMember);

    // 🔹 Update an existing RAC Member
    @SqlUpdate("UPDATE rac_members SET name = :name, role = :role, department = :department, updated_at = current_timestamp WHERE id = :id")
    void updateRACMember(@BindBean RACMember racMember);

    // 🔹 Delete a RAC Member by ID
    @SqlUpdate("DELETE FROM rac_members WHERE id = :id")
    void deleteRACMember(@Bind("id") Long id);

    // 🔹 Find a RAC Member by ID
    @SqlQuery("SELECT * FROM rac_members WHERE id = :id")
    Optional<RACMember> findById(@Bind("id") Long id);

    // 🔹 Find all RAC Members
    @SqlQuery("SELECT * FROM rac_members ORDER BY created_at DESC")
    List<RACMember> findAll();

    @SqlQuery("SELECT * FROM rac_members ORDER BY created_at DESC LIMIT :size OFFSET :offset")
    @RegisterBeanMapper(RACMember.class)
    List<RACMember> findAllPaginated(@Bind("size") int size, @Bind("offset") int offset);

    @SqlQuery("SELECT COUNT(*) FROM rac_members")
    int getTotalRACMembers();

}

