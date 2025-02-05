package com.quickweather.service.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.UUID;

public class CustomUserDetails implements UserDetails {

    private final Long userId;
    private final String username;
    private final String name;
    private final String email;
    private final String password;
    private final boolean isLocked;
    private final boolean isEnabled;
    private final Collection<? extends GrantedAuthority> authorities;
    private final UUID uuid;

    public CustomUserDetails(Long userId,
                             String username,
                             String name,
                             String email,
                             String password,
                             boolean isLocked,
                             boolean isEnabled,
                             Collection<? extends GrantedAuthority> authorities,
                             UUID uuid) {
        this.userId = userId;
        this.username = username;
        this.name = name;
        this.email = email;
        this.password = password;
        this.isLocked = isLocked;
        this.isEnabled = isEnabled;
        this.authorities = authorities;
        this.uuid = uuid;
    }

    // Dodatkowy konstruktor bez hasła, imienia i emaila
//    public CustomUserDetails(Long userId, String username, Collection<? extends GrantedAuthority> authorities) {
//        this(userId, username, null, null, null, false, true, authorities);
//    }


    public Long getUserId() {
        return userId;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public boolean isLocked() {
        return isLocked;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public UUID getUuid() {
        return uuid;
    }
}
