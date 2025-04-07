package com.app.bdc_backend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Constraint(validatedBy = StrongPassword.StrongPasswordValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StrongPassword {
    String message() default "Mật khẩu tối thiểu 8 ký tự, phải bao gồm ký tự hoa, thường, chữ số và ký tự đặc biệt";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            return value != null && value.matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!*()]).{8,}$");
        }
    }
}