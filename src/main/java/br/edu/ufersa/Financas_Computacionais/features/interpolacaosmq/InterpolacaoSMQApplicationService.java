package br.edu.ufersa.Financas_Computacionais.features.interpolacaosmq;

import br.edu.ufersa.Financas_Computacionais.features.interpolacaosmq.dto.InterpolacaoRequestSMQ;
import br.edu.ufersa.Financas_Computacionais.features.interpolacaosmq.dto.InterpolacaoResponseSMQ;
import org.springframework.stereotype.Service;

@Service
public class InterpolacaoSMQApplicationService {

    private final InterpolacaoSMQDomainService domainService;

    public InterpolacaoSMQApplicationService(InterpolacaoSMQDomainService domainService) {
        this.domainService = domainService;
    }

    public InterpolacaoResponseSMQ executar(InterpolacaoRequestSMQ request) {
        if (request == null
                || request.metodo() == null
                || request.x() == null
                || request.y() == null) {
            throw new IllegalArgumentException(
                    "Método, x e y são obrigatórios."
            );
        }

        double resultado = domainService.calcular(
                request.metodo(),
                request.x(),
                request.y(),
                request.valor()
        );

        return new InterpolacaoResponseSMQ(
                request.metodo(),
                request.valor(),
                resultado
        );
    }
}



