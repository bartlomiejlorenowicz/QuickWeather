package com.quickweather.repository;

import com.quickweather.entity.User;
import com.quickweather.entity.UserSearchHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface UserSearchHistoryRepository extends JpaRepository<UserSearchHistory, Long> {

    Page<UserSearchHistory> findByUserId(Long userId, Pageable pageable);

    boolean existsByUserAndCityAndSearchedAtAfter(User user, String city, LocalDateTime timestamp);


}
