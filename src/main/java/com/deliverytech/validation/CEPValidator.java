package com.deliverytech.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CEPValidator implements ConstraintValidator<ValidCEP, String> {

    private static final String CEP_PATTERN = "^\\d{5}-?\\d{3}$";

    @Override
    public boolean isValid(String cep, ConstraintValidatorContext context) {
        if (cep == null || cep.isBlank()) {
            return true; // deixa @NotBlank cuidar de campos obrigatórios
        }
        return cep.matches(CEP_PATTERN);
    }
}
