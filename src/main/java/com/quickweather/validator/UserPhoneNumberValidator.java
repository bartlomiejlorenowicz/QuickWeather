package com.quickweather.validator;

import com.quickweather.dto.UserDto;

public class UserPhoneNumberValidator implements Validator {

    private Validator nextValidator;

    @Override
    public void setNext(Validator nextValidator) {
        this.nextValidator = nextValidator;
    }

    @Override
    public void validate(UserDto userDto) {
        String phoneNumber = userDto.getPhoneNumber();
        if (phoneNumber == null) {
            throw new IllegalArgumentException("phone number is null");
        }
        if (phoneNumber.length() < 10 || phoneNumber.length() > 15) {
            throw new IllegalArgumentException("phone number must be between 10 and 15 digits");
        }
    }
}
