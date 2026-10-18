package br.edu.ufersa.Financas_Computacionais.features.amortizacao;

import br.edu.ufersa.Financas_Computacionais.features.amortizacao.dto.AmortizacaoRequestDTO;
import br.edu.ufersa.Financas_Computacionais.features.amortizacao.dto.TabelaAmortizacaoDTO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/amortizacoes")
public class AmortizacaoRestController {

    private final SistemaAmortizacaoService service;

    public AmortizacaoRestController(SistemaAmortizacaoService service) {
        this.service = service;
    }

    @PostMapping
    public TabelaAmortizacaoDTO calcular(@Valid @RequestBody AmortizacaoRequestDTO request) {
        return service.calcular(request);
    }
}