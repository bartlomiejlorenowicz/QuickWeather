package com.quickweather.validator;

import com.quickweather.dto.UserDto;
import com.quickweather.repository.UserCreationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Slf4j
@Component
public class UserEmailValidator extends AbstractValidator {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";

    private UserCreationRepository userCreationRepository;

    public UserEmailValidator(UserCreationRepository userCreationRepository) {
        this.userCreationRepository = userCreationRepository;
    }

    @Override
    protected void doValidate(UserDto userDto) {
        String email = userDto.getEmail();
        if (isNull(email)) {
            throw new IllegalArgumentException("email is null");
        }
        boolean incorrectEmail = !email.matches(EMAIL_REGEX);
        if (incorrectEmail) {
            throw new IllegalArgumentException("email is not valid");
        }
        boolean emailExistInDatabase = userCreationRepository.existsByEmail(email);
        if (emailExistInDatabase) {
            throw new IllegalArgumentException("the given e-mail exists in the database");
        }
    }
}
