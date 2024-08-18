package com.quickweather.validator;

import com.quickweather.dto.UserDto;
import com.quickweather.repository.UserCreationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
@Slf4j
@Component
public class UserEmailValidator implements Validator {

    private Validator nextValidator;

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";

    private UserCreationRepository userCreationRepository;

    public UserEmailValidator(UserCreationRepository userCreationRepository) {
        this.userCreationRepository = userCreationRepository;
    }

    @Override
    public void setNext(Validator nextValidator) {
        this.nextValidator = nextValidator;
    }

    @Override
    public void validate(UserDto userDto) {
        String email = userDto.getEmail();
        if (email == null) {
            throw new IllegalArgumentException("email is null");
        }
        if (email.matches(EMAIL_REGEX)) {
            throw new IllegalArgumentException("email is not valid");
        }
        if (userCreationRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("the given e-mail exists in the database");
        }
    }
}
