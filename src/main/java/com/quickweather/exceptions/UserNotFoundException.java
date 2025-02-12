package com.quickweather.exceptions;

import lombok.Getter;

/**
 * Exception thrown when a user cannot be found.
 * <p>
 * This exception is used to indicate that a user lookup has failed based on the provided criteria.
 * It contains an error type that specifies the nature of the error and an optional user identifier,
 * such as an email or user ID, that was used during the lookup.
 * </p>
 *
 * @see UserErrorType
 */
@Getter
public class UserNotFoundException extends RuntimeException {

    /**
     * The error type associated with this exception.
     */
    private final UserErrorType userErrorType;

    /**
     * The identifier of the user that was not found (e.g., email or user ID).
     * This value may be {@code null} if no identifier was provided.
     */
    private final String userId;

    /**
     * Constructs a new {@code UserNotFoundException} with the specified error type and detail message.
     *
     * @param userErrorType the type of error (for example, {@code USER_NOT_FOUND})
     * @param message       the detail message explaining the error
     */
    public UserNotFoundException(UserErrorType userErrorType, String message) {
        super(message);
        this.userErrorType = userErrorType;
        this.userId = null;
    }

    /**
     * Constructs a new {@code UserNotFoundException} with the specified error type, detail message,
     * and user identifier.
     *
     * @param userErrorType the type of error (for example, {@code USER_NOT_FOUND})
     * @param message       the detail message explaining the error
     * @param userId        the identifier of the user that was not found
     */
    public UserNotFoundException(UserErrorType userErrorType, String message, String userId) {
        super(message);
        this.userErrorType = userErrorType;
        this.userId = userId;
    }

}
