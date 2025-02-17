package com.manuu.phdreport.database;

import com.manuu.phdreport.entity.UserVerification;
import lombok.RequiredArgsConstructor;
import org.jdbi.v3.core.Jdbi;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserVerificationDaoImpl {

    private final Jdbi jdbi;

    public void saveOtp(String email, String otp, LocalDateTime expiresAt) {
        jdbi.useExtension(UserVerificationDao.class, dao -> dao.saveOtp(email, otp, expiresAt));
    }

    public Optional<UserVerification> findByEmail(String email) {
        return jdbi.withExtension(UserVerificationDao.class, dao -> dao.findByEmail(email));
    }

    public Optional<UserVerification> validateOtp(Long userId, String otp) {
        return jdbi.withExtension(UserVerificationDao.class, dao -> dao.validateOtp(userId, otp));
    }

    public void deleteByEmail(String email) {
        jdbi.useExtension(UserVerificationDao.class, dao -> dao.deleteByEmail(email));
    }

    public void updateOtp(String email, String newOtp, LocalDateTime expiresAt) {
        jdbi.useExtension(UserVerificationDao.class, dao -> dao.updateOtp(email, newOtp, expiresAt));
    }

}

