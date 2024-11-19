package com.quickweather.dto.accuweather;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AccuWeatherDailyResponse {

    @JsonProperty("Headline")
    private Headline headline;

    @JsonProperty("DailyForecasts")
    private List<DailyForecast> dailyForecasts;

    @Getter
    @Setter
    public static class Headline {
        @JsonProperty("EffectiveDate")
        private String effectiveDate;

        @JsonProperty("EffectiveEpochDate")
        private long effectiveEpochDate;

        @JsonProperty("Severity")
        private int severity;

        @JsonProperty("Text")
        private String text;

        @JsonProperty("Category")
        private String category;

        @JsonProperty("EndDate")
        private String endDate;

        @JsonProperty("EndEpochDate")
        private long endEpochDate;

        @JsonProperty("MobileLink")
        private String mobileLink;

        @JsonProperty("Link")
        private String link;
    }

    @Getter
    @Setter
    public static class DailyForecast {
        @JsonProperty("Date")
        private String date;

        @JsonProperty("EpochDate")
        private long epochDate;

        @JsonProperty("Temperature")
        private Temperature temperature;

        @JsonProperty("Day")
        private DayNight day;

        @JsonProperty("Night")
        private DayNight night;

        @JsonProperty("Sources")
        private List<String> sources;

        @JsonProperty("MobileLink")
        private String mobileLink;

        @JsonProperty("Link")
        private String link;

        @Getter
        @Setter
        public static class Temperature {
            @JsonProperty("Minimum")
            private UnitValue minimum;

            @JsonProperty("Maximum")
            private UnitValue maximum;

            @Getter
            @Setter
            public static class UnitValue {
                @JsonProperty("Value")
                private double value;

                @JsonProperty("Unit")
                private String unit;

                @JsonProperty("UnitType")
                private int unitType;
            }
        }

        @Getter
        @Setter
        public static class DayNight {
            @JsonProperty("Icon")
            private int icon;

            @JsonProperty("IconPhrase")
            private String iconPhrase;

            @JsonProperty("HasPrecipitation")
            private boolean hasPrecipitation;

            @JsonProperty("PrecipitationType")
            private String precipitationType;

            @JsonProperty("PrecipitationIntensity")
            private String precipitationIntensity;
        }
    }
}
