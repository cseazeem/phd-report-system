package com.manuu.phdreport.database;

import com.manuu.phdreport.entity.UserVerification;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.time.LocalDateTime;
import java.util.Optional;

@RegisterBeanMapper(UserVerification.class)
public interface UserVerificationDao {

    @SqlUpdate("INSERT INTO user_verification (email, otp, expires_at) VALUES (:email, :otp, :expiresAt) " +
            "ON CONFLICT (id) DO UPDATE SET otp = :otp, expires_at = :expiresAt")
    @GetGeneratedKeys
    Long saveOtp(@Bind("email") String email, @Bind("otp") String otp, @Bind("expiresAt") LocalDateTime expiresAt);

    @SqlQuery("SELECT * FROM user_verification WHERE email = :email")
    Optional<UserVerification> findByEmail(@Bind("email") String email);

    @SqlQuery("SELECT * FROM user_verification WHERE user_id = :userId AND otp = :otp AND expires_at > NOW()")
    Optional<UserVerification> validateOtp(@Bind("userId") Long userId, @Bind("otp") String otp);

    @SqlUpdate("DELETE FROM user_verification WHERE email = :email")
    void deleteByEmail(@Bind("email") String email);

    @SqlUpdate("UPDATE user_verification SET otp = :otp, expires_at = :expiresAt WHERE email = :email")
    void updateOtp(@Bind("email") String email, @Bind("otp") String otp, @Bind("expiresAt") LocalDateTime expiresAt);

}

