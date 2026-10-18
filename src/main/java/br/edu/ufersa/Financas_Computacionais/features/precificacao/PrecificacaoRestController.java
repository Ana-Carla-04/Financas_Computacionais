package br.edu.ufersa.Financas_Computacionais.features.precificacao;

import br.edu.ufersa.Financas_Computacionais.features.precificacao.dto.ArvoreBinomialRequestDTO;
import br.edu.ufersa.Financas_Computacionais.features.precificacao.dto.CoxRossRubinsteinRequestDTO;
import br.edu.ufersa.Financas_Computacionais.features.precificacao.dto.CoxRossRubinsteinResponseDTO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/precificacao")
public class PrecificacaoRestController {

    private final ArvoreBinomialService arvoreBinomialService;
    private final CoxRossRubinsteinService coxRossRubinsteinService;

    public PrecificacaoRestController(ArvoreBinomialService arvoreBinomialService,
                                      CoxRossRubinsteinService coxRossRubinsteinService) {
        this.arvoreBinomialService = arvoreBinomialService;
        this.coxRossRubinsteinService = coxRossRubinsteinService;
    }

    @PostMapping("/arvore-binomial/call")
    public double precificarCall(@Valid @RequestBody ArvoreBinomialRequestDTO request) {
        return arvoreBinomialService.precificarCall(request);
    }

    @PostMapping("/arvore-binomial/put")
    public double precificarPut(@Valid @RequestBody ArvoreBinomialRequestDTO request) {
        return arvoreBinomialService.precificarPut(request);
    }

    @PostMapping("/cox-ross-rubinstein")
    public CoxRossRubinsteinResponseDTO calcularCoxRossRubinstein(
            @Valid @RequestBody CoxRossRubinsteinRequestDTO request) {
        return coxRossRubinsteinService.calcular(request);
    }
}