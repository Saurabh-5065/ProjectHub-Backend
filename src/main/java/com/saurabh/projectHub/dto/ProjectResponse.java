package com.saurabh.projectHub.dto;

import com.saurabh.projectHub.model.Project.Priority;
import com.saurabh.projectHub.model.Project.Status;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProjectResponse {

    private String id;
    private String name;
}