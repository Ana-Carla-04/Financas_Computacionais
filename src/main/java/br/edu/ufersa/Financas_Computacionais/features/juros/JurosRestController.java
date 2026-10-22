package br.edu.ufersa.Financas_Computacionais.features.juros;

import br.edu.ufersa.Financas_Computacionais.features.juros.dto.EquivalenciaCapitaisRequestDTO;
import br.edu.ufersa.Financas_Computacionais.features.juros.dto.EquivalenciaCapitaisResponseDTO;
import br.edu.ufersa.Financas_Computacionais.features.juros.dto.JurosRequestDTO;
import br.edu.ufersa.Financas_Computacionais.features.juros.dto.JurosResponseDTO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/juros")
public class JurosRestController {

    private final JurosService jurosService;
    private final EquivalenciaCapitaisService equivalenciaService;

    public JurosRestController(JurosService jurosService,
                               EquivalenciaCapitaisService equivalenciaService) {
        this.jurosService = jurosService;
        this.equivalenciaService = equivalenciaService;
    }

    @PostMapping("/valor-futuro")
    public JurosResponseDTO valorFuturo(@Valid @RequestBody JurosRequestDTO request) {
        return jurosService.calcularValorFuturo(request);
    }

    @PostMapping("/valor-presente")
    public JurosResponseDTO valorPresente(@Valid @RequestBody JurosRequestDTO request) {
        return jurosService.calcularValorPresente(request);
    }

    @PostMapping("/equivalencia-capitais")
    public EquivalenciaCapitaisResponseDTO equivalenciaCapitais(
            @Valid @RequestBody EquivalenciaCapitaisRequestDTO request) {
        return equivalenciaService.calcular(request);
    }
}