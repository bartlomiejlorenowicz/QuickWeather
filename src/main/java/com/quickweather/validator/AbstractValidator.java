package com.quickweather.validator;

import com.quickweather.dto.UserDto;

public abstract class AbstractValidator implements Validator {

    protected Validator nextValidator;

    @Override
    public void setNext(Validator nextValidator) {
        this.nextValidator = nextValidator;
    }

    @Override
    public void validate(UserDto userDto) {
        doValidate(userDto);
        if (nextValidator != null) {
            nextValidator.validate(userDto);
        }
    }

    protected abstract void doValidate(UserDto userDto);
}
