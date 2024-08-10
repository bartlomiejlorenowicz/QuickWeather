package com.quickweather.validator;

import com.quickweather.dto.UserDto;
import com.quickweather.repository.UserCreationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
@Slf4j
public class UserValidator {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
    private static final String PHONE_NUMBER_REGEX = "^[0-9\\-\\s]+$";
    private static final String PASSWORD_REGEX = ".*[!@#$%^&*(),.?\\\":{}|<>].*";
    private UserCreationRepository userCreationRepository;

    public UserValidator(UserCreationRepository userCreationRepository) {
        this.userCreationRepository = userCreationRepository;
    }

    public void validate(UserDto userDto) {
        log.info("Starting validation user with email: " + userDto.getEmail());
        validateFirstName(userDto.getFirstName());
        validateLastName(userDto.getLastName());
        validatePassword(userDto.getPassword());
        validateEmail(userDto.getEmail());
        validatePhoneNumber(userDto.getPhoneNumber());
        log.info("Ending validation user with email" + userDto.getEmail());
    }

    public void validateFirstName(String firstName) {
        if (firstName.length() < 2 || firstName.length() > 30) {
            throw new IllegalArgumentException("first name must be between 2 and 30 characters");
        }
    }

    public void validateLastName(String lastName) {
        if (lastName.length() < 2 || lastName.length() > 30) {
            throw new IllegalArgumentException("last name must be between 2 and 30 characters");
        }
    }

    public void validatePassword(String password) {
        if (password.length() < 8) {
            throw new IllegalArgumentException("password must be minimum 8 characters long");
        }

        if (!Pattern.compile(PASSWORD_REGEX).matcher(password).matches()) {
            throw new IllegalArgumentException("password does not contain a special character");
        }
    }

    public void validateEmail(String email) {
        if (userCreationRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email is already in use.");
        }

        if (!Pattern.compile(EMAIL_REGEX).matcher(email).matches()) {
            throw new IllegalArgumentException("email is not valid");
        }
    }

    public void validatePhoneNumber(String phoneNumber) {
        if (phoneNumber.length() < 10 || phoneNumber.length() > 15) {
            throw new IllegalArgumentException("Phone number must be between 10 and 15 characters");
        }

        if (!Pattern.compile(PHONE_NUMBER_REGEX).matcher(phoneNumber).matches()) {
            throw new IllegalArgumentException("Phone number is not valid");
        }
    }
}
