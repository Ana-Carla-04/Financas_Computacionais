package br.edu.ufersa.Financas_Computacionais.features.avaliacao;

import br.edu.ufersa.Financas_Computacionais.features.avaliacao.dto.ComparacaoInvestimentosRequestDTO;
import br.edu.ufersa.Financas_Computacionais.features.avaliacao.dto.InvestimentoRequestDTO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/avaliacoes/investimentos")
public class AvaliacaoRestController {

    private final ComparacaoInvestimentosService service;

    public AvaliacaoRestController(ComparacaoInvestimentosService service) {
        this.service = service;
    }

    @PostMapping("/analise")
    public ComparacaoInvestimentosService.ResultadoInvestimento analisar(
            @Valid @RequestBody InvestimentoRequestDTO request) {
        return service.analisar(request.nome(), request.fluxosCaixa(), request.taxaDesconto());
    }

    @PostMapping("/comparacao")
    public ComparacaoInvestimentosService.Comparacao comparar(
            @Valid @RequestBody ComparacaoInvestimentosRequestDTO request) {
        return service.comparar(
                request.primeiro().nome(), request.primeiro().fluxosCaixa(),
                request.segundo().nome(), request.segundo().fluxosCaixa(),
                request.primeiro().taxaDesconto()
        );
    }
}