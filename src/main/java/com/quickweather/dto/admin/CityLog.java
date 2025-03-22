package com.quickweather.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CityLog {

    private String city;
    private long count;

}
