package br.edu.ufersa.Financas_Computacionais.features.interpolacao;
import br.edu.ufersa.Financas_Computacionais.features.interpolacao.dto.InterpolacaoRequest;
import br.edu.ufersa.Financas_Computacionais.features.interpolacao.dto.InterpolacaoResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/interpolacao")
public class InterpolacaoController {

    //var e construtor necessários para alterações
    private final InterpolacaoApplicationService applicationService;
    public InterpolacaoController(InterpolacaoApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    public ResponseEntity<InterpolacaoResponse> calcular(@Valid @RequestBody InterpolacaoRequest request) {
        InterpolacaoResponse response = applicationService.calcular(request); //chama o método em application service
        return ResponseEntity.ok(response);
    }
}

