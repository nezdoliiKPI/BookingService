package com.example.lab2.security;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.lab2.entity.User;
import com.example.lab2.entity.User.UserStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserDetailsImpl implements UserDetails { 
    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { 
        return Stream.of(user.getUserRole().name().split(",")).map(SimpleGrantedAuthority::new).collect(Collectors.toList()); 
    }

    public Long getId() { return user.getUserId(); }

    @Override
    public String getPassword() { return user.getPassword(); }

    @Override
    public String getUsername() { return user.getEmail(); }

    @Override
    public boolean isAccountNonExpired() { 
        return user.getUserStatus() == UserStatus.ACTIVE; 
    }

    @Override
    public boolean isAccountNonLocked() { 
        return user.getUserStatus() == UserStatus.ACTIVE; 
    }

    @Override
    public boolean isCredentialsNonExpired() { 
        return user.getUserStatus() == UserStatus.ACTIVE; 
    }

    @Override
    public boolean isEnabled() { 
        return user.getUserStatus() == UserStatus.ACTIVE; 
    }

}