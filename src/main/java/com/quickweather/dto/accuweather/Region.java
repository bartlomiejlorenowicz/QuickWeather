package com.quickweather.dto.accuweather;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Region {

    @JsonProperty("LocalizedName")
    private String localizedName;

    public String getLocalizedName() {
        return localizedName;
    }

    public void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }
}
