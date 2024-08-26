package com.quickweather.validator;

import com.quickweather.dto.UserDto;

import static java.util.Objects.isNull;

public class UserPhoneNumberValidator implements Validator {

    private Validator nextValidator;

    @Override
    public void setNext(Validator nextValidator) {
        this.nextValidator = nextValidator;
    }

    @Override
    public void validate(UserDto userDto) {
        String phoneNumber = userDto.getPhoneNumber();
        if (isNull(phoneNumber)) {
            throw new IllegalArgumentException("phone number is null");
        }
        boolean phoneNumberTooShort = phoneNumber.length() < 10;
        if (phoneNumberTooShort) {
            throw new IllegalArgumentException("phone number must have at least 10 digits");
        }
        boolean phoneNumberTooLong = phoneNumber.length() > 15;
        if (phoneNumberTooLong) {
            throw new IllegalArgumentException("phone number must have maximum 15 digits");
        }
    }
}
