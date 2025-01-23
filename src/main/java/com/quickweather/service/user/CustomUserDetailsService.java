package com.quickweather.service.user;

import com.quickweather.entity.User;
import com.quickweather.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));

        // Mapowanie ról na SimpleGrantedAuthority
        var authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleType()))
                .toList();

        return new CustomUserDetails(
                user.getId().toString(),
                user.getEmail(),
                user.getFirstName(),
                user.getEmail(),
                user.getPassword(),
                user.isLocked(),
                user.isEnabled(),
                authorities
        );
    }

    public CustomUserDetails createCustomUserDetails(User user) {
        log.debug("Creating CustomUserDetails for user with ID: {}", user.getId());

        // Mapowanie ról użytkownika na SimpleGrantedAuthority
        var authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleType()))
                .collect(Collectors.toList());

        // Tworzenie obiektu CustomUserDetails
        return new CustomUserDetails(
                user.getId().toString(),
                user.getEmail(),
                user.getFirstName(),
                user.getEmail(),
                user.getPassword(),
                user.isLocked(),
                user.isEnabled(),
                authorities
        );
    }
}
