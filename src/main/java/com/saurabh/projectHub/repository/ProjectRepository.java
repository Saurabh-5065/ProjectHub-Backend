package com.saurabh.projectHub.repository;

import com.saurabh.projectHub.model.Project;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProjectRepository extends MongoRepository<Project, String> {
    List<Project> findByTeamLeadId(String teamLeadId);   // get all projects by team lead
    List<Project> findByMembersId(String memberId);      // get all projects a user is member of
}