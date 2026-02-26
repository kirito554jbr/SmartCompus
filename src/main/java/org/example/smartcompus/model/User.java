package org.example.smartcompus.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.smartcompus.model.enums.UserRole;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idUser;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private boolean isEnabeld;
    @Enumerated(EnumType.STRING)
    private UserRole role;
}
