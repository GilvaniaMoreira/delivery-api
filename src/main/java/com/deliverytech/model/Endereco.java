package com.deliverytech.model;

import com.deliverytech.validation.ValidCEP;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Endereco {

    private String rua;
    private String numero;
    private String bairro;
    private String cidade;
    private String estado;

    @ValidCEP
    private String cep;
}
