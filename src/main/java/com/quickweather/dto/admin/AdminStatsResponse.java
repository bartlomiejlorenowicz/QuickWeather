package com.quickweather.dto.admin;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AdminStatsResponse {
    private long activeUsers;
    private long totalUsers;
    private long inactiveUsers;
    private List<CityLog> cityLogs;
    private TopWeather topWeather;
}
