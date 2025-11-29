package com.masbuilders.masbuildersbackend.entity;


import com.masbuilders.masbuildersbackend.enums.Role;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "users")
public class User {

    @Id
    private String id;

    private String name;
    private String email;
    private String password;

    private Role role; // ADMIN / SELLER / BUYER

    private String phoneNumber;
}

