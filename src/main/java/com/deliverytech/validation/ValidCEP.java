package com.deliverytech.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CEPValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCEP {
    String message() default "CEP inválido. Formato esperado: 12345-678 ou 12345678";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
