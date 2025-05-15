package com.manuu.phdreport.service;

import com.manuu.phdreport.database.PhDScholarRACMemberDao;
import com.manuu.phdreport.entity.PhDScholarRACMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PhDScholarRACService {

    private final PhDScholarRACMemberDao scholarRACMemberDao;

    public Long assignRacMember(Long scholarId, Long racMemberId, String role) {
        return scholarRACMemberDao.insert(scholarId, racMemberId, role);
    }

    public List<PhDScholarRACMember> getAssignedRacMembers(Long scholarId) {
        return scholarRACMemberDao.findByScholarId(scholarId);
    }

    public void removeRacMember(Long scholarId, Long racMemberId) {
        scholarRACMemberDao.deleteByScholarAndRacMember(scholarId, racMemberId);
    }
}

