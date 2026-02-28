package com.deliverytech.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = TelefoneValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTelefone {
    String message() default "Telefone inválido. Formato esperado: (11) 91234-5678 ou 11912345678";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
