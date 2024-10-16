package com.quickweather.dto.accuweather;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AccuWeatherResponse {

    @JsonProperty("LocalizedName")
    private String localizedName;

    @JsonProperty("Region")
    private Region region;

    @JsonProperty("Country")
    private Country country;


    public String getLocalizedName() {
        return localizedName;
    }

    public void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }
}
