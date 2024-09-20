package com.quickweather.validator;

import com.quickweather.dto.UserDto;
import com.quickweather.exceptions.InvalidEmailAlreadyExistException;
import com.quickweather.exceptions.InvalidEmailException;
import com.quickweather.repository.UserCreationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Slf4j
@Component
public class UserEmailValidator extends Validator {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";

    private final UserCreationRepository userCreationRepository;

    public UserEmailValidator(UserCreationRepository userCreationRepository) {
        this.userCreationRepository = userCreationRepository;
    }

    @Override
    public void validate(UserDto userDto) {
        String email = userDto.getEmail();
        if (isNull(email)) {
            throw new InvalidEmailException("email is null");
        }
        boolean incorrectEmail = !email.matches(EMAIL_REGEX);
        if (incorrectEmail) {
            throw new InvalidEmailException("email is not valid");
        }
        boolean emailExistInDatabase = userCreationRepository.existsByEmail(email);
        if (emailExistInDatabase) {
            throw new InvalidEmailAlreadyExistException("the given e-mail exists in the database");
        }
        validateNext(userDto);
    }
}
