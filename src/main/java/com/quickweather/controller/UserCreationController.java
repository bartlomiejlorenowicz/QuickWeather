package com.quickweather.controller;

import com.quickweather.dto.UserDto;
import com.quickweather.repository.UserCreationRepository;
import com.quickweather.service.UserCreationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user/register")
public class UserCreationController {

   private UserCreationService userCreationService;

    public UserCreationController(UserCreationService userCreationService) {
        this.userCreationService = userCreationService;
    }

    @PostMapping
    public void register(@RequestBody UserDto userDto) {
        userCreationService.createUser(userDto);
    }


}
