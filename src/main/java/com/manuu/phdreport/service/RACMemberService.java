package com.manuu.phdreport.service;

import com.manuu.phdreport.database.RACMemberDao;
import com.manuu.phdreport.entity.RACMember;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RACMemberService {
    private final RACMemberDao racMemberDao;

    // 🔹 Update an existing RAC Member
    public void updateRACMember(RACMember racMember) {
        if (racMemberDao.findById(racMember.getId()).isEmpty()) {
            throw new IllegalStateException("RAC Member not found.");
        }
        racMemberDao.updateRACMember(racMember);
    }

    // 🔹 Delete a RAC Member
    public void deleteRACMember(Long id) {
        if (racMemberDao.findById(id).isEmpty()) {
            throw new IllegalStateException("RAC Member not found.");
        }
        racMemberDao.deleteRACMember(id);
    }

    // 🔹 Get a RAC Member by ID
    public Optional<RACMember> getRACMemberById(Long id) {
        return racMemberDao.findById(id);
    }

    // 🔹 Get all RAC Members
    public Page<RACMember> getAllRACMembers(int page, int size) {
        int offset = (page - 1) * size; // Calculate offset for pagination
        List<RACMember> racMembers = racMemberDao.findAllPaginated(size, offset);
        int totalRecords = racMemberDao.getTotalRACMembers();

        return new PageImpl<>(racMembers, PageRequest.of(page, size), totalRecords);
    }

}
