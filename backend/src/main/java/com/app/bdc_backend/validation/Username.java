package com.app.bdc_backend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Constraint(validatedBy = Username.UsernameValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Username {
    String message() default "Username tối thiểu 6 ký tự, chỉ được chứa chữ cái và số";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class UsernameValidator implements ConstraintValidator<Username, String> {
        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            return value != null && value.matches("^[a-zA-Z0-9]{6,}$");
        }
    }
}
