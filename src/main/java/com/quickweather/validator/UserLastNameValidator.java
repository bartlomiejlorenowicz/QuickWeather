package com.quickweather.validator;

import com.quickweather.dto.UserDto;

public class UserLastNameValidator implements Validator {

    private Validator nextValidator;

    @Override
    public void setNext(Validator nextValidator) {
        this.nextValidator = nextValidator;
    }

    @Override
    public void validate(UserDto userDto) {
        String lastName = userDto.getLastName();
        if (lastName == null) {
            throw new IllegalArgumentException("lastname i null");
        }
        if (lastName.length() < 2 || lastName.length() > 30) {
            throw new IllegalArgumentException("last name must be between 2 and 30 characters");
        }
        if (nextValidator != null) {
            nextValidator.validate(userDto);
        }
    }
}
