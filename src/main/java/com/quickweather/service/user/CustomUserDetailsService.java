package com.quickweather.service.user;

import com.quickweather.entity.User;
import com.quickweather.repository.UserCreationRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserCreationRepository userRepository;

    public CustomUserDetailsService(UserCreationRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Fetch user from the database
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));

        // Map the single role to Spring Security's GrantedAuthority
        var authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());

        // Return UserDetails object
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword()) // Encoded password
                .authorities(authority) // Assign authority
                .accountLocked(user.isLocked()) // Map locked status
                .disabled(!user.isEnabled()) // Map enabled status
                .build();
    }
}
