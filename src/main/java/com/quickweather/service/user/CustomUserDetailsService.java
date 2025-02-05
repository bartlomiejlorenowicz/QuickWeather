package com.quickweather.service.user;

import com.quickweather.entity.User;
import com.quickweather.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Attempting to load user by username/email: {}", username);

        if (username == null || username.isEmpty()) {
            log.error("Username is null or empty!");
            throw new UsernameNotFoundException("Email is null or empty");
        }

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));

        log.debug("User found: {}", user.getEmail());

        var authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleType()))
                .collect(Collectors.toList());

        // Upewnij się, że obiekt User posiada metodę getUuid()
        return new CustomUserDetails(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getEmail(),
                user.getPassword(),
                user.isLocked(),
                user.isEnabled(),
                authorities,
                user.getUuid()
        );
    }

    public CustomUserDetails createCustomUserDetails(User user) {
        log.debug("Creating CustomUserDetails for user with ID: {}", user.getId());

        var authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleType()))
                .collect(Collectors.toList());

        return new CustomUserDetails(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getEmail(),
                user.getPassword(),
                user.isLocked(),
                user.isEnabled(),
                authorities,
                user.getUuid()
        );
    }

}
