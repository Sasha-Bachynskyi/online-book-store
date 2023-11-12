package com.onlinebookstore.validation;

import com.onlinebookstore.dto.user.UserRegistrationRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchValidation
        implements ConstraintValidator<PasswordMatch, UserRegistrationRequestDto> {
    @Override
    public boolean isValid(UserRegistrationRequestDto requestDto,
                           ConstraintValidatorContext constraintValidatorContext) {
        return requestDto.getPassword() != null
                && requestDto.getRepeatPassword() != null
                && requestDto.getPassword().equals(requestDto.getRepeatPassword());
    }
}
