package com.manuu.phdreport.configuration;

import com.manuu.phdreport.database.*;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.spi.JdbiPlugin;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import javax.sql.DataSource;
import java.util.List;

@Configuration
public class JdbiConfiguration {
    @Bean
    public Jdbi jdbi(DataSource ds, List<JdbiPlugin> jdbiPlugins, List<RowMapper<?>> rowMappers) {
        TransactionAwareDataSourceProxy proxy = new TransactionAwareDataSourceProxy(ds);
        Jdbi jdbi = Jdbi.create(proxy);

        // Register all available plugins
//        log.info("[I27] Installing plugins... ({} found)", jdbiPlugins.size());
        jdbiPlugins.forEach(jdbi::installPlugin);

        // Register all available rowMappers
//        log.info("[I31] Installing rowMappers... ({} found)", rowMappers.size());
        rowMappers.forEach(jdbi::registerRowMapper);
        return jdbi;
    }

    @Bean
    public JdbiPlugin sqlObjectPlugin() {
        return new SqlObjectPlugin();
    }

    @Bean
    public UserDao userDao(Jdbi jdbi) {
        return jdbi.onDemand(UserDao.class);
    }
    @Bean
    public UserVerificationDao userVerificationDao(Jdbi jdbi) {
        return jdbi.onDemand(UserVerificationDao.class);
    }
    @Bean
    public ScholarDao phDScholarDao(Jdbi jdbi) {
        return jdbi.onDemand(ScholarDao.class);
    }
    @Bean
    public CoordinatorDao coordinatorDao(Jdbi jdbi) {
        return jdbi.onDemand(CoordinatorDao.class);
    }
    @Bean
    public ReportDao reportDao(Jdbi jdbi) {
        return jdbi.onDemand(ReportDao.class);
    }
    @Bean
    public ReportApprovalDao reportApprovalDao(Jdbi jdbi) {
        return jdbi.onDemand(ReportApprovalDao.class);
    }
    @Bean
    public SignatureDao signatureDao(Jdbi jdbi) {
        return jdbi.onDemand(SignatureDao.class);
    }
    @Bean
    public NoticeDao noticeDao(Jdbi jdbi) {
        return jdbi.onDemand(NoticeDao.class);
    }
    @Bean
    public RACMemberDao racMemberDao(Jdbi jdbi) {
        return jdbi.onDemand(RACMemberDao.class);
    }
    @Bean
    public PhDScholarRACMemberDao  phDScholarRACMemberDao(Jdbi jdbi) {
        return jdbi.onDemand(PhDScholarRACMemberDao.class);
    }
}

