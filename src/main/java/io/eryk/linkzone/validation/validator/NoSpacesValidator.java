package io.eryk.linkzone.validation.validator;

import io.eryk.linkzone.validation.annotation.NoSpacesConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NoSpacesValidator implements ConstraintValidator<NoSpacesConstraint, String> {

    @Override
    public void initialize(NoSpacesConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && value.length() == value.replaceAll("\\s", "").length();
    }
}
