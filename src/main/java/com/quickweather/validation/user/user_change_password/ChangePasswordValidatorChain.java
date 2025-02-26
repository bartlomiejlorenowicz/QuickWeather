package com.quickweather.validation.user.user_change_password;

import org.springframework.stereotype.Component;

@Component
public class ChangePasswordValidatorChain {

    public ChangePasswordValidator buildChain() {
        return ChangePasswordValidator.link(
                new CurrentPasswordValidator(),
                new ConfirmPasswordValidator(),
                new NewPasswordDifferentValidator()
        );
    }
}
