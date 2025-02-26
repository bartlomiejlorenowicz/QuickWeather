package com.quickweather.dto.admin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminStatsResponse {
    private long activeUsers;
    private long totalUsers;
}
