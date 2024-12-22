package com.example.lab2.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UserFormValidator.class)
@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface UserFormConstraint {
    String message() default "Недомустиме значення введеного паролю чи імейлу";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
