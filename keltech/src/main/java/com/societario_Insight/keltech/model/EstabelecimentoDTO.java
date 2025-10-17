package com.societario_Insight.keltech.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class EstabelecimentoDTO {

    private String cnpj;
    private String tipo;
    private String nomeFantasia;
    private String situacaoCadastral;
    private String dataSituacaoCadastral;
    private String dataInicioAtividade;

    private String tipoLogradouro;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cep;

    private String cidade;
    private Integer cidadeIbgeId;
    private String estadoSigla;
    private String estadoNome;

    private String telefone1;
    private String telefone2;
    private String email;

    private AtividadeDTO atividadePrincipal;
    private List<AtividadeDTO> atividadesSecundarias;
}
