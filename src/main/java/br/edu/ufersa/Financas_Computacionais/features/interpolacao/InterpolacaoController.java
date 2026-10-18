package br.edu.ufersa.Financas_Computacionais.features.interpolacao;
import br.edu.ufersa.Financas_Computacionais.features.interpolacao.dto.InterpolacaoRequest;
import br.edu.ufersa.Financas_Computacionais.features.interpolacao.dto.InterpolacaoResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/interpolacao")
public class InterpolacaoController {

    private final InterpolacaoApplicationService applicationService;

    public InterpolacaoController(
            InterpolacaoApplicationService applicationService
    ) {
        this.applicationService = applicationService;
    }

    @PostMapping
    public ResponseEntity<InterpolacaoResponse> calcular(
            @RequestBody InterpolacaoRequest request
    ) {
        InterpolacaoResponse response =
                applicationService.calcular(request);

        return ResponseEntity.ok(response);
    }
}

