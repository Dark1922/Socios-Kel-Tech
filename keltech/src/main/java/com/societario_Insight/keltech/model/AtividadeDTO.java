package com.societario_Insight.keltech.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AtividadeDTO {

    private String id;
    private String secao;
    private String divisao;
    private String grupo;
    private String classe;
    private String subclasse;
    private String descricao;
}
