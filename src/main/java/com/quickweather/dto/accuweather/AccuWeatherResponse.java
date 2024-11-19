package com.quickweather.dto.accuweather;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AccuWeatherResponse {

    @JsonProperty("Key")
    private String key;

    @JsonProperty("LocalizedName")
    private String localizedName;

    @JsonProperty("Region")
    private Region region;

    @JsonProperty("Country")
    private Country country;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

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

    public static class Region {
        @JsonProperty("ID")
        private String id;

        @JsonProperty("LocalizedName")
        private String localizedName;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getLocalizedName() {
            return localizedName;
        }

        public void setLocalizedName(String localizedName) {
            this.localizedName = localizedName;
        }
    }

    public static class Country {
        @JsonProperty("ID")
        private String id;

        @JsonProperty("LocalizedName")
        private String localizedName;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getLocalizedName() {
            return localizedName;
        }

        public void setLocalizedName(String localizedName) {
            this.localizedName = localizedName;
        }
    }
}
