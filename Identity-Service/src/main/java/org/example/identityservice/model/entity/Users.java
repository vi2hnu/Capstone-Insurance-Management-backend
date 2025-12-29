package org.example.identityservice.model.entity;

import lombok.Data;
import org.example.identityservice.model.enums.Gender;
import org.example.identityservice.model.enums.Roles;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "Users")
public class Users {
    @Id
    String id;
    String name;

    @Indexed(unique = true)
    String username;

    @Indexed(unique = true)
    String email;
    String password;
    Date lastPasswordChange;
    Gender gender;
    Roles role;

    public Users(String username, String email, String password,Date lastPasswordChange){
        this.username = username;
        this.email = email;
        this.password = password;
        this.lastPasswordChange = lastPasswordChange;
    }
}
