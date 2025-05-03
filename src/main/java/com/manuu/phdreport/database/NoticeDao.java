package com.manuu.phdreport.database;

import com.manuu.phdreport.entity.Notice;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;

public interface NoticeDao {

//        @SqlUpdate("INSERT INTO notices (uploaded_by_id, role, scholar_id, notice_path, title, description, created_at) " +
//                "VALUES (:uploadedById, :role, :scholarId, :noticePath, :title, :description, now())")
//        void insertNotice(@BindBean Notice notice);
//
//        @SqlQuery("SELECT * FROM notices WHERE scholar_id = :scholarId ORDER BY created_at DESC")
//        @RegisterBeanMapper(Notice.class)
//        List<Notice> getNoticesForScholar(@Bind("scholarId") Long scholarId);

    @SqlUpdate("INSERT INTO notices (uploaded_by_id, role, notice_path, title, description, created_at) " +
            "VALUES (:uploadedById, :role, :noticePath, :title, :description, current_timestamp)")
    void insertNotice(@BindBean Notice notice);

    @SqlQuery("SELECT * FROM notices ORDER BY created_at DESC LIMIT :size OFFSET :offset")
    @RegisterBeanMapper(Notice.class)
    List<Notice> getAllNotices(@Bind("offset") int offset, @Bind("size") int size);

    @SqlQuery("SELECT * FROM notices where id=:id")
    @RegisterBeanMapper(Notice.class)
    Notice getNoticeFile(Long id);


}
