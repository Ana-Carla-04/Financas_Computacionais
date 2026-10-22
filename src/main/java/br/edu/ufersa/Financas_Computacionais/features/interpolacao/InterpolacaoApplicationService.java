package br.edu.ufersa.Financas_Computacionais.features.interpolacao;
import br.edu.ufersa.Financas_Computacionais.features.interpolacao.dto.InterpolacaoRequest;
import br.edu.ufersa.Financas_Computacionais.features.interpolacao.dto.InterpolacaoResponse;
import org.springframework.stereotype.Service;

// Valida dados e coordena os cálculos

@Service
public class InterpolacaoApplicationService {

    private final InterpolacaoDomainService domainService; // var necessária
    public InterpolacaoApplicationService (InterpolacaoDomainService domainService) {
        this.domainService = domainService;
    } // construtor necessário

    public InterpolacaoResponse calcular(InterpolacaoRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Corpo da requisição ausente.");
        }
        if (request.metodo() == null) {
            throw new IllegalArgumentException("Informe LAGRANGE ou NEWTON.");
        }
        if (request.prazoDesejado() == null) {
            throw new IllegalArgumentException("Informe o prazo desejado.");
        }

        double prazo = request.prazoDesejado();
        double resultado = switch (request.metodo()) {
            case LAGRANGE -> domainService.lagrange(request.prazos(), request.taxas(), prazo);
            case NEWTON   -> domainService.newton(request.prazos(), request.taxas(), prazo);
        };

        return new InterpolacaoResponse(request.metodo(), prazo, resultado);
    }
}

