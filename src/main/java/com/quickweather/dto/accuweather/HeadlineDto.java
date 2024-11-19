package com.quickweather.dto.accuweather;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HeadlineDto {
    private String text;
    private String category;
    private String effectiveDate;
}