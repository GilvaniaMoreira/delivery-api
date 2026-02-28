package com.deliverytech.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TelefoneValidator implements ConstraintValidator<ValidTelefone, String> {

    // Aceita formatos: (11) 91234-5678, (11) 1234-5678, 11912345678, 1112345678
    private static final String TELEFONE_PATTERN = "^\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}$";

    @Override
    public boolean isValid(String telefone, ConstraintValidatorContext context) {
        if (telefone == null || telefone.isBlank()) {
            return true; // deixa @NotBlank cuidar de campos obrigatórios
        }
        return telefone.matches(TELEFONE_PATTERN);
    }
}
