package com.quickweather.controller;

import com.quickweather.dto.user.UserDto;
import com.quickweather.service.user.UserCreationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/user/register")
public class UserCreationController {

    private final UserCreationService userCreationService;

    public UserCreationController(UserCreationService userCreationService) {
        this.userCreationService = userCreationService;
    }

    @PostMapping
    public ResponseEntity<Void> register(@RequestBody UserDto userDto) {
        userCreationService.createUser(userDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
