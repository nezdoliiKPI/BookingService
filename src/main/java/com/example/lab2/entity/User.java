package com.example.lab2.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @Builder.Default
    @Column(name = "user_id")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long userId = null;

    private String email;

    @Column(name = "user_password")
    private String password;
    
    @Column(name = "user_name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "card_code")
    private String cardCode;
   
    @Builder.Default
    @Column(name = "owner_score")
    private Byte ownerScore = USER_NOT_SCORED;

    @JsonIgnore
    @Builder.Default
    @Column(name = "user_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus = UserStatus.ACTIVE;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "user_role", nullable = false)
    private UserRole userRole = UserRole.USER;

    public enum UserStatus {
        ACTIVE, ON_DELETE;
    }

    public enum UserRole {
        USER, ADMIN;
    }

    @JsonIgnore
    @Transient
    static public final Byte USER_NOT_SCORED = null;
}
