package com.quickweather.dto.weatherDtos.airpollution;

public class AirPollutionItems {
    private MainAqi main;
    private ComponentsDto components;

    public MainAqi getMain() {
        return main;
    }

    public void setMain(MainAqi main) {
        this.main = main;
    }

    public ComponentsDto getComponents() {
        return components;
    }

    public void setComponents(ComponentsDto components) {
        this.components = components;
    }
}
