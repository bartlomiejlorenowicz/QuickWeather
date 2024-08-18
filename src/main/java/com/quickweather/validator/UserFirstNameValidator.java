package com.quickweather.validator;

import com.quickweather.dto.UserDto;

public class UserFirstNameValidator implements Validator {

    private Validator nextValidator;

    @Override
    public void setNext(Validator nextValidator) {
        this.nextValidator = nextValidator;
    }

    @Override
    public void validate(UserDto userDto) {
        String firstName = userDto.getFirstName();
        if (firstName == null) {
            throw new IllegalArgumentException("first name is null");
        }
        if (firstName.length() < 2 || firstName.length() > 30) {
            throw new IllegalArgumentException("first name must be between 2 and 30 characters");
        }
        if (nextValidator != null) {
            nextValidator.validate(userDto);
        }
    }
}
