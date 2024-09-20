package com.quickweather.validator;

import com.quickweather.dto.UserDto;
import com.quickweather.exceptions.InvalidPhoneNumberException;

import static java.util.Objects.isNull;

public class UserPhoneNumberValidator extends Validator {

    @Override
    public void validate(UserDto userDto) {
        String phoneNumber = userDto.getPhoneNumber();
        if (isNull(phoneNumber)) {
            throw new InvalidPhoneNumberException("phone number is null");
        }
        boolean phoneNumberTooShort = phoneNumber.length() < 10;
        if (phoneNumberTooShort) {
            throw new InvalidPhoneNumberException("phone number must have at least 10 digits");
        }
        boolean phoneNumberTooLong = phoneNumber.length() > 15;
        if (phoneNumberTooLong) {
            throw new InvalidPhoneNumberException("phone number must have maximum 15 digits");
        }
        validateNext(userDto);
    }
}
