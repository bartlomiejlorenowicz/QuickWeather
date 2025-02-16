package com.quickweather.validation.location;

public class CoordinatesValidatorChain {

    public static CoordinatesValidator buildChain () {
        return CoordinatesValidator.link(
                new EmptyCoordinatesValidator(),
                new NumberFormatCoordinatesValidator(),
                new CoordinatesRangeValidator()
        );
    }
}
