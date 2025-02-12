package com.quickweather.dto.weatherDtos.airpollution;

import java.util.List;

public class AirPollutionResponseDto {

    private List<AirPollutionItems> list;

    public List<AirPollutionItems> getList() {
        return list;
    }

    public void setList(List<AirPollutionItems> list) {
        this.list = list;
    }
}
