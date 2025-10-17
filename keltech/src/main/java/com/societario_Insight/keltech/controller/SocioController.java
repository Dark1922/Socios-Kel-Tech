package com.societario_Insight.keltech.controller;


import com.societario_Insight.keltech.model.SocioDetalheDTO;
import com.societario_Insight.keltech.model.SocioResumoDTO;
import com.societario_Insight.keltech.service.SocioService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/socios")
@RequiredArgsConstructor
@Validated
public class SocioController {

    private final SocioService socioService;

    @GetMapping
    public ResponseEntity<List<SocioResumoDTO>> listar(
            @RequestParam(name = "participacaoMin", defaultValue = "0") @Min(0) @Max(100) double participacaoMin) {
        return ResponseEntity.ok(socioService.listarPorParticipacaoMin(participacaoMin));
    }

    @GetMapping("/{cnpj}")
    public ResponseEntity<SocioDetalheDTO> detalhar(
            @PathVariable @Pattern(regexp = "\\d{14}|\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}") String cnpj) {
        return ResponseEntity.ok(socioService.detalharPorCnpj(cnpj));
    }
}

