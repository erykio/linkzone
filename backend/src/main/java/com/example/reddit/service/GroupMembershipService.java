package com.example.reddit.service;

import com.example.reddit.exception.NotFoundException;
import com.example.reddit.model.GroupMembership;
import com.example.reddit.repository.GroupMembershipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GroupMembershipService {

    private final GroupMembershipRepository groupMembershipRepository;

    @Autowired
    public GroupMembershipService(GroupMembershipRepository groupMembershipRepository) {
        this.groupMembershipRepository = groupMembershipRepository;
    }

    public List<GroupMembership> findAll() {
        return groupMembershipRepository.findAll();
    }

    public Page<GroupMembership> findAll(Pageable pageable) {
        return groupMembershipRepository.findAll(pageable);
    }

    public GroupMembership save(GroupMembership groupMembership) {
        return groupMembershipRepository.save(groupMembership);
    }

    public Optional<GroupMembership> findById(Long aLong) {
        return groupMembershipRepository.findById(aLong);
    }

    public boolean existsById(Long aLong) {
        return groupMembershipRepository.existsById(aLong);
    }

    public long count() {
        return groupMembershipRepository.count();
    }

    public void delete(GroupMembership groupMembership) {
        groupMembershipRepository.delete(groupMembership);
    }

    public List<GroupMembership> findByAccountUsername(String username) {
        return groupMembershipRepository.findByAccountUsername(username);
    }

    public GroupMembership findByAccountUsernameAndGroupName(String accountUsername, String groupName) {
        return groupMembershipRepository.findByAccountUsernameAndGroupName(accountUsername, groupName)
                .orElseThrow(() -> new NotFoundException(GroupMembership.class, groupName));
    }

    public boolean isUserSubbedToGroup(String accountUsername, String groupName) {
        return groupMembershipRepository.findByAccountUsernameAndGroupName(accountUsername, groupName).isPresent();
    }

    public boolean existsByGroupIdAndAccountId(Long groupId, Long accountId) {
        return groupMembershipRepository.existsByGroupIdAndAccountId(groupId, accountId);
    }

    public void deleteAll() {
        groupMembershipRepository.deleteAll();
    }
}
