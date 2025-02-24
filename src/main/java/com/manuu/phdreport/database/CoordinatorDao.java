package com.manuu.phdreport.database;


import com.manuu.phdreport.entity.Coordinator;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.Optional;

public interface CoordinatorDao {

    @SqlUpdate("INSERT INTO coordinators (user_id, name, email, department, created_at) " +
            "VALUES (:userId, :name, :email, :department, current_timestamp)")
    void insertCoordinator(@BindBean Coordinator coordinator);


    @SqlQuery("SELECT * FROM coordinators WHERE user_id = :userId")
    @RegisterBeanMapper(Coordinator.class)
    Optional<Coordinator> findById(@Bind("userId") Long userId);

}
