package com.saurabh.projectHub.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

@Data
@Document(collection = "users")
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    private String name;

    @Indexed(unique = true)
    private String email;

    private String password; // Bcrypt hashed

    private String role = "ROLE_USER";
}