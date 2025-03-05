package com.quickweather.service.admin;

import com.quickweather.domain.User;
import com.quickweather.dto.admin.AdminStatsResponse;
import com.quickweather.dto.admin.AdminUserDTO;
import com.quickweather.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    public AdminStatsResponse getDashboardStats() {
        long activeUsers = userRepository.countByIsEnabledTrue();
        long totalUsers = userRepository.count();

        AdminStatsResponse stats = new AdminStatsResponse();
        stats.setActiveUsers(activeUsers);
        stats.setTotalUsers(totalUsers);
        // dodać statystyki, np. dzienne loginy, raporty.
        return stats;
    }

    public Page<AdminUserDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(user -> new AdminUserDTO(
                        user.getId(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        user.isEnabled()
                ));
    }

    public void updateUserStatus(Long userId, boolean enabled) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEnabled(enabled);
        userRepository.save(user);
    }

}
