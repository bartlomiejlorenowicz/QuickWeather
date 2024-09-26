package com.quickweather.validator;

import com.quickweather.dto.UserDto;

import static java.util.Objects.isNull;

public abstract class Validator {
    private Validator next;

    public static Validator link(Validator first, Validator... chain) {
        Validator head = first;
        for (Validator nextInChain : chain) {
            head.next = nextInChain;
            head = nextInChain;
        }
        return first;
    }

    public abstract void validate(UserDto userDto);

    protected void validateNext(UserDto userDto) {
        if (isNull(next)) {
            return;
        }
        next.validate(userDto);
    }
}
