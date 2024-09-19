package com.quickweather.controller;

import com.quickweather.dto.UserDto;
import com.quickweather.service.UserCreationService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/user/register")
public class UserCreationController {

   private final UserCreationService userCreationService;

    public UserCreationController(UserCreationService userCreationService) {
        this.userCreationService = userCreationService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> register(@RequestBody UserDto userDto) {
        userCreationService.createUser(userDto);

        Map<String, String> response = new HashMap<>();
        response.put("message", "User successfully registered");

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

}
