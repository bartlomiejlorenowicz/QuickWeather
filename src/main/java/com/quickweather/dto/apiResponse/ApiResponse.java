package com.quickweather.dto.apiResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
public class ApiResponse {
    private String message;
    private Instant timestamp;
    private OperationType operationType;

    public static ApiResponse buildApiResponse(String message, OperationType operationType) {
        return new ApiResponse(
                message,
                Instant.now(),
                operationType
        );
    }
}
