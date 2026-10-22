package br.edu.ufersa.Financas_Computacionais.features.interpolacaosmq;

import br.edu.ufersa.Financas_Computacionais.features.interpolacaosmq.dto.InterpolacaoRequestSMQ;
import br.edu.ufersa.Financas_Computacionais.features.interpolacaosmq.dto.InterpolacaoResponseSMQ;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/interpolacao-smq")
public class InterpolacaoSMQController {

    private final InterpolacaoSMQApplicationService applicationService;

    public InterpolacaoSMQController(InterpolacaoSMQApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    public InterpolacaoResponseSMQ interpolar(@RequestBody InterpolacaoRequestSMQ request) {
        try {
            return applicationService.executar(request);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
}

