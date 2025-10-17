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


        JsonNode est = receita.path("estabelecimento");

        String tipoLogradouro = est.path("tipo_logradouro").asText("");   // ex.: RODOVIA
        String logradouro     = est.path("logradouro").asText("");        // ex.: ANHANGUERA KM 98
        String numero         = est.path("numero").asText("");            // ex.: SN
        String complemento    = est.path("complemento").asText("");       // ex.: KM 98
        String bairro         = est.path("bairro").asText("");            // ex.: JARDIM EULINA
        String cidade         = est.path("cidade").path("nome").asText("");// ex.: Campinas
        String uf             = est.path("estado").path("sigla").asText("");// ex.: SP
        String cepRaw         = est.path("cep").asText("");
        String cepFmt         = formatCep(cepRaw);   // ex.: 13065900

        List<String> linha1 = new ArrayList<>();
        if (notBlank(tipoLogradouro)) linha1.add(tipoLogradouro);
        if (notBlank(logradouro))     linha1.add(logradouro);
        if (notBlank(numero) && !"SN".equalsIgnoreCase(numero.trim())) linha1.add(numero);
        if (notBlank(complemento))    linha1.add(complemento.trim().replaceAll("\\s+", " "));

        String enderecoCompleto = String.join(" ", linha1);

        List<String> partes = new ArrayList<>();
        if (notBlank(enderecoCompleto)) partes.add(enderecoCompleto);
        if (notBlank(bairro))           partes.add(bairro);
        if (notBlank(cidade) || notBlank(uf)) {
            partes.add((notBlank(cidade) ? cidade : "") + (notBlank(uf) ? " - " + uf : ""));
        }
        if (notBlank(cepFmt))           partes.add(cepFmt);
        partes.add("Brasil");

        String address = String.join(", ", partes);

        String mapaUrl = mapsUrlFromAddress(address);

        List<EstabelecimentoDTO> estabelecimentos = parseEstabelecimentos(receita);

        String naturezaJuridica = getText(receita, "natureza_juridica", "descricao");

        String nomeFantasia = estabelecimentos.isEmpty() ? "" : Optional.ofNullable(estabelecimentos.get(0).getNomeFantasia()).orElse("");
        String situacaoCadastral = estabelecimentos.isEmpty() ? "" : Optional.ofNullable(estabelecimentos.get(0).getSituacaoCadastral()).orElse("");

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


    private String mapsUrlFromAddress(String address) {
        if (address == null || address.isBlank()) {
            return "https://www.google.com/maps?output=embed";
        }
        String q = URLEncoder.encode(address.trim(), StandardCharsets.UTF_8);
        return "https://www.google.com/maps?q=" + q + "&output=embed";
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


    private static boolean notBlank(String s) {
        return s != null && !s.isBlank();
    }

    private static String digits(String s) {
        return s == null ? "" : s.replaceAll("\\D", "");
    }

    private static String formatCep(String cepDigits) {
        if (cepDigits == null) return "";
        String d = digits(cepDigits);
        return d.length() == 8 ? d.substring(0,5) + "-" + d.substring(5) : d;
    }

}
