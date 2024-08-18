package com.quickweather.validator;

import com.quickweather.dto.UserDto;

public interface Validator {

    void setNext(Validator nextValidator);
    void validate(UserDto userDto);
}
