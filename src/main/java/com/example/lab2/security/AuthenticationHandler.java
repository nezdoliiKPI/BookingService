package com.example.lab2.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import com.example.lab2.entity.User.UserRole;

public class AuthenticationHandler {

    private Boolean hasAccess = true;
    private List<Boolean> operations = new ArrayList<>();

    UserDetailsImpl userDetails;
    private Set<String> roles;

    AuthenticationHandler(Authentication authentication) {
        userDetails = (UserDetailsImpl)authentication.getPrincipal();
        roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
    }

    public static AuthenticationHandler mayUser(Authentication authentication) {
        return new AuthenticationHandler(authentication);
    }

    public AuthenticationHandler or() {
        operations.add(hasAccess);
        hasAccess = true;
        return this;
    }

    public AuthenticationHandler hasUserRole() {
        hasAccess = hasAccess && roles.contains(UserRole.USER.name());
        return this;
    }

    public AuthenticationHandler hasAdminRole() {
        hasAccess = hasAccess && roles.contains(UserRole.ADMIN.name());
        return this;
    }

    public AuthenticationHandler isOwner(Long ownerId) {
        hasAccess = hasAccess && userDetails.getId().equals(ownerId);
        return this;
    }

    public Boolean handle() {
        for (Boolean operation : operations) 
            hasAccess = hasAccess || operation;

        return hasAccess;
    }
}
