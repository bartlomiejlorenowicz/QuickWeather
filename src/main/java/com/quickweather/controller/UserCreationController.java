package com.quickweather.controller;

import com.quickweather.dto.UserDto;
import com.quickweather.service.UserCreationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user/register")
public class UserCreationController {

   private UserCreationService userCreationService;

    public UserCreationController(UserCreationService userCreationService) {
        this.userCreationService = userCreationService;
    }

    @PostMapping
    public void register(@Valid @RequestBody UserDto userDto) {
        userCreationService.createUser(userDto);
    }


}
