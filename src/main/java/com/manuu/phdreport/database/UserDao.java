package com.manuu.phdreport.database;

import com.manuu.phdreport.entity.User;
import com.manuu.phdreport.entity.UserResponse;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;

@RegisterBeanMapper(User.class)
public interface UserDao {

    @SqlQuery("SELECT * FROM users WHERE email = :email")
    @RegisterBeanMapper(User.class)
    User findByEmail(@Bind("email") String email);

    @SqlUpdate("UPDATE users SET status = :status, created_at = current_timestamp WHERE id = :id")
    void updateUserStatus(@Bind("id") Long id, @Bind("status") String status);

    @SqlUpdate("INSERT INTO users (email, password, role, status, created_at, updated_at) " +
            "VALUES (:email, :password, :role, :status, current_timestamp, current_timestamp)")
    @GetGeneratedKeys
    Long createUser(@BindBean User user);

    @SqlQuery("SELECT * FROM users ORDER BY created_at DESC LIMIT :size OFFSET :offset")
    @RegisterBeanMapper(UserResponse.class)
    List<UserResponse> findAllPaginated(@Bind("size") int size, @Bind("offset") int offset);

    @SqlQuery("SELECT COUNT(*) FROM users")
    int countUsers();

    @SqlQuery("SELECT * FROM users WHERE id = :id")
    @RegisterBeanMapper(User.class)
    User findById(@Bind("id") Long id);
}

