package com.quickweather.service.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private final String userId;
    private final String username;
    private final String name;
    private final String email;
    private final String password;
    private final boolean isLocked;
    private final boolean isEnabled;
    private final Collection<? extends GrantedAuthority> authorities;

    // Główny konstruktor
    public CustomUserDetails(String userId, String username, String name, String email, String password,
                             boolean isLocked, boolean isEnabled, Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.username = username;
        this.name = name;
        this.email = email;
        this.password = password;
        this.isLocked = isLocked;
        this.isEnabled = isEnabled;
        this.authorities = authorities;
    }

    // Dodatkowy konstruktor bez hasła, imienia i emaila
    public CustomUserDetails(String userId, String username, Collection<? extends GrantedAuthority> authorities) {
        this(userId, username, null, null, null, false, true, authorities);
    }

    // Gettery
    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}
