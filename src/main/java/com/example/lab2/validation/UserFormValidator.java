package com.example.lab2.validation;

import java.util.regex.Pattern;

import com.example.lab2.entity.User;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UserFormValidator implements ConstraintValidator<UserFormConstraint, User>{
    private final String regexEmailPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@" 
                                      + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
    private final String regexPasswordPattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,14}$";

    private final Pattern emailPattern = Pattern.compile(regexEmailPattern);
    private final Pattern passwordPattern = Pattern.compile(regexPasswordPattern);

    private boolean emailIsValid(String email) {
        //username@domain.com
        //user.name@domain.com
        //user-name@domain.com
        //username@domain.co.in
        //user_name@domain.com
        return email != null && emailPattern.matcher(email).matches();
    }

    private boolean passwordIsValid(String password) {
        //"abc123333333333"
        //"a1b2c3444444444444"
        //"aas2c3555555555"
        return password != null && passwordPattern.matcher(password).matches();
    }

    @Override
    public boolean isValid(User userForm, ConstraintValidatorContext context) {
        boolean result = true;

        if (userForm == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Потрібно ввести дані користувача")
                   .addPropertyNode("userForm")
                   .addConstraintViolation();
            return false;
        }
        if (!emailIsValid(userForm.getEmail())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Потрібно ввести валідний імейл")
                   .addPropertyNode("email")
                   .addConstraintViolation();
            result = false;
        }
        if (!passwordIsValid(userForm.getPassword())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Потрібно ввести валідний пароль")
                   .addPropertyNode("password")
                   .addConstraintViolation();
            result = false;
        }

        if (userForm.getName() == null || 
            userForm.getSurname() == null || 
            userForm.getCardCode() == null) {

            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Потрібно ввести всі дані")
                   .addPropertyNode("data")
                   .addConstraintViolation();
            result = false;
        }

        return result;
    }
}
