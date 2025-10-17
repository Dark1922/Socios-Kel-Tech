package com.societario_Insight.keltech.model;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SocioDetalheDTO {

    private String cnpj;
    private String nome;
    private Double participacao;
    private String cep;

    private String razaoSocial;
    private String nomeFantasia;
    private String naturezaJuridica;
    private String situacaoCadastral;

    private String mapaEmbedUrl;

    private List<EstabelecimentoDTO> estabelecimentos;
}
