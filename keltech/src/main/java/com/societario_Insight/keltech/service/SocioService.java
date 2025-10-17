package com.societario_Insight.keltech.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.societario_Insight.keltech.client.KelTechClient;
import com.societario_Insight.keltech.client.CnpjReceitaClient;
import com.societario_Insight.keltech.model.AtividadeDTO;
import com.societario_Insight.keltech.model.EstabelecimentoDTO;
import com.societario_Insight.keltech.model.SocioDetalheDTO;
import com.societario_Insight.keltech.model.SocioResumoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class SocioService {

    private final KelTechClient kelTechClient;
    private final CnpjReceitaClient receitaClient;

    public List<SocioResumoDTO> listarPorParticipacaoMin(double participacaoMin) {

        JsonNode root = kelTechClient.buscarQuadroSocietario();

        JsonNode lista = root.path("mix").path("quadroSocietario").path("data").path("quadroSocietario");

        if (!lista.isArray()) {
            throw new IllegalStateException("Estrutura inesperada do JSON da KelTech");
        }

        List<SocioResumoDTO> out = new ArrayList<>();
        for (JsonNode socio : lista) {
            double particip = socio.path("participacao").asDouble(0.0);
            if (particip >= participacaoMin) {
                SocioResumoDTO dto = SocioResumoDTO.builder()
                        .cnpj(cleanDigits(socio.path("cnpjEmpresa").asText("")))
                        .nome(socio.path("nome").asText(""))
                        .participacao(particip)
                        .cep(socio.path("cep").asText(""))
                        .build();
                out.add(dto);
            }
        }
        out.sort(Comparator.comparing(SocioResumoDTO::getParticipacao).reversed());
        return out;
    }

    public SocioDetalheDTO detalharPorCnpj(String cnpj) {

        String cnpjDigits = cleanDigits(cnpj);

        JsonNode root = kelTechClient.buscarQuadroSocietario();
        JsonNode lista = root.path("mix").path("quadroSocietario").path("data").path("quadroSocietario");

        JsonNode socioBase = StreamSupport.stream(lista.spliterator(), false)
                .filter(n -> cleanDigits(n.path("cnpjEmpresa").asText("")).equals(cnpjDigits))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Sócio não encontrado na base KelTech"));

        JsonNode receita = receitaClient.buscarPorCnpj(cnpjDigits);

        String cep = receita.path("estabelecimento").path("cep").asText("");

        List<EstabelecimentoDTO> estabelecimentos = parseEstabelecimentos(receita);

        String naturezaJuridica = getText(receita, "natureza_juridica", "descricao");

        String nomeFantasia = estabelecimentos.isEmpty() ? "" : Optional.ofNullable(estabelecimentos.get(0).getNomeFantasia()).orElse("");
        String situacaoCadastral = estabelecimentos.isEmpty() ? "" : Optional.ofNullable(estabelecimentos.get(0).getSituacaoCadastral()).orElse("");

        String mapaUrl = buildMapsEmbedUrl(cep);

        return SocioDetalheDTO.builder()
                .cnpj(cnpjDigits)
                .nome(socioBase.path("nome").asText(""))
                .participacao(socioBase.path("participacao").asDouble(0.0))
                .cep(cep)
                .razaoSocial(getText(receita, "razao_social"))
                .nomeFantasia(nomeFantasia)
                .naturezaJuridica(naturezaJuridica)
                .situacaoCadastral(situacaoCadastral)
                .mapaEmbedUrl(mapaUrl)
                .estabelecimentos(estabelecimentos)
                .build();
    }

    private String cleanDigits(String s) {
        return s == null ? "" : s.replaceAll("\\D", "");
    }

    private String buildMapsEmbedUrl(String cep) {
        if (cep == null || cep.isBlank()) return "https://www.google.com/maps?output=embed";
        return "https://www.google.com/maps?q=" + URLEncoder.encode(cep, StandardCharsets.UTF_8) + "&output=embed";
    }

    private static String getText(JsonNode node, String... path) {
        JsonNode curr = node;
        for (String p : path) curr = curr.path(p);
        return curr.isMissingNode() || curr.isNull() ? "" : curr.asText("");
    }

    private static Integer getInt(JsonNode node, String... path) {
        JsonNode curr = node;
        for (String p : path) curr = curr.path(p);
        return curr.isMissingNode() || curr.isNull() ? null : curr.asInt();
    }

    private static AtividadeDTO toAtividade(JsonNode n) {
        if (n == null || n.isMissingNode() || n.isNull()) return null;
        return AtividadeDTO.builder()
                .id(getText(n, "id"))
                .secao(getText(n, "secao"))
                .divisao(getText(n, "divisao"))
                .grupo(getText(n, "grupo"))
                .classe(getText(n, "classe"))
                .subclasse(getText(n, "subclasse"))
                .descricao(getText(n, "descricao"))
                .build();
    }

    private static EstabelecimentoDTO toEstabelecimento(JsonNode e) {
        return EstabelecimentoDTO.builder()
                .cnpj(getText(e, "cnpj"))
                .tipo(getText(e, "tipo"))
                .nomeFantasia(getText(e, "nome_fantasia"))
                .situacaoCadastral(getText(e, "situacao_cadastral"))
                .dataSituacaoCadastral(getText(e, "data_situacao_cadastral"))
                .dataInicioAtividade(getText(e, "data_inicio_atividade"))

                .tipoLogradouro(getText(e, "tipo_logradouro"))
                .logradouro(getText(e, "logradouro"))
                .numero(getText(e, "numero"))
                .complemento(getText(e, "complemento"))
                .bairro(getText(e, "bairro"))
                .cep(getText(e, "cep"))

                .cidade(getText(e, "cidade", "nome"))
                .cidadeIbgeId(getInt(e, "cidade", "ibge_id"))
                .estadoSigla(getText(e, "estado", "sigla"))
                .estadoNome(getText(e, "estado", "nome"))

                .telefone1(getText(e, "ddd1") + getText(e, "telefone1"))
                .telefone2(getText(e, "ddd2") + getText(e, "telefone2"))
                .email(getText(e, "email"))

                .atividadePrincipal(toAtividade(e.path("atividade_principal")))
                .atividadesSecundarias(
                        StreamSupport.stream(e.path("atividades_secundarias").spliterator(), false)
                                .map( SocioService::toAtividade )
                                .filter(Objects::nonNull)
                                .toList()
                )
                .build();
    }

    private static List<EstabelecimentoDTO> parseEstabelecimentos(JsonNode receita) {
        JsonNode est = receita.path("estabelecimento");
        if (est.isMissingNode() || est.isNull()) return List.of();

        if (est.isArray()) {
            return StreamSupport.stream(est.spliterator(), false)
                    .map(SocioService::toEstabelecimento)
                    .toList();
        } else {
            return List.of(toEstabelecimento(est));
        }
    }
}
