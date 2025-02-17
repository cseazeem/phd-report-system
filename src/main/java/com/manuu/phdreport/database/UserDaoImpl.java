package com.manuu.phdreport.database;

import com.manuu.phdreport.entity.User;
import com.manuu.phdreport.entity.UserResponse;
import lombok.RequiredArgsConstructor;
import org.jdbi.v3.core.Jdbi;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserDaoImpl {

    private final Jdbi jdbi;
    private final UserDao userDao;

    public User findByEmail(String email) {
        return jdbi.withExtension(UserDao.class, dao -> dao.findByEmail(email));
    }

    public void updateUserStatus(Long id, String status) {
        jdbi.useExtension(UserDao.class, dao -> dao.updateUserStatus(id, status));
    }

    public Long createUser(User user) {
        return jdbi.withExtension(UserDao.class, dao -> dao.createUser(user));
    }

    public PageImpl<UserResponse> findAll(Pageable pageable) {
        int totalCount = userDao.countUsers();
        List<UserResponse> users = userDao.findAllPaginated(pageable.getPageSize(), (int) pageable.getOffset());
        return new PageImpl<>(users, pageable, totalCount);
    }

    public User findById(Long id) {
        return jdbi.withExtension(UserDao.class, dao -> dao.findById(id));
    }
}

