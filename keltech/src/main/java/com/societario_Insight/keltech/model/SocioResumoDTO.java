package com.societario_Insight.keltech.model;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SocioResumoDTO {

    private String cnpj;
    private String nome;
    private Double participacao;
    private String cep;
}
