package com.quickweather.dto.weatherDtos.forecast;

import com.quickweather.dto.weatherDtos.weather.Temp;
import com.quickweather.dto.weatherDtos.weather.Weather;

import java.util.List;

public class ForecastDailyDto {
    private Temp temp;
    private double pressure;
    private double humidity;
    private List<Weather> weather;

    public Temp getTemp() {
        return temp;
    }

    public void setTemp(Temp temp) {
        this.temp = temp;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public List<Weather> getWeather() {
        return weather;
    }

    public void setWeather(List<Weather> weather) {
        this.weather = weather;
    }
}
