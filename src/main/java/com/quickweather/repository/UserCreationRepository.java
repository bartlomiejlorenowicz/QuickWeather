package com.quickweather.repository;

import com.quickweather.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCreationRepository extends JpaRepository<User, Long> {


}
