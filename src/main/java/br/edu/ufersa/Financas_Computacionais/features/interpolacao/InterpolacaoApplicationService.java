package br.edu.ufersa.Financas_Computacionais.features.interpolacao;
import br.edu.ufersa.Financas_Computacionais.features.interpolacao.dto.InterpolacaoRequest;
import br.edu.ufersa.Financas_Computacionais.features.interpolacao.dto.InterpolacaoResponse;
import org.springframework.stereotype.Service;

@Service
public class InterpolacaoApplicationService {

    private final InterpolacaoDomainService domainService;

    public InterpolacaoApplicationService(
            InterpolacaoDomainService domainService
    ) {
        this.domainService = domainService;
    }

    public InterpolacaoResponse calcular(
            InterpolacaoRequest request
    ) {
        if (request.prazos() == null
                || request.taxas() == null
                || request.prazos().isEmpty()
                || request.prazos().size() != request.taxas().size()) {
            throw new IllegalArgumentException(
                    "Informe a mesma quantidade de prazos e taxas."
            );
        }

        if (request.prazos().size() < 2) {
            throw new IllegalArgumentException(
                    "Informe pelo menos dois pontos."
            );
        }

        for (int i = 0; i < request.prazos().size(); i++) {
            if (!Double.isFinite(request.prazos().get(i))
                    || !Double.isFinite(request.taxas().get(i))) {
                throw new IllegalArgumentException(
                        "Os prazos e as taxas devem ser números finitos."
                );
            }

            for (int j = 0; j < i; j++) {
                if (request.prazos().get(i)
                        .equals(request.prazos().get(j))) {
                    throw new IllegalArgumentException(
                            "Os prazos devem ser distintos."
                    );
                }
            }
        }

        if (!Double.isFinite(request.prazoDesejado())) {
            throw new IllegalArgumentException(
                    "O prazo desejado deve ser um número finito."
            );
        }

        String metodo = request.metodo();

        if (metodo == null) {
            throw new IllegalArgumentException(
                    "Informe LAGRANGE ou NEWTON."
            );
        }

        double resultado;

        switch (metodo.trim().toUpperCase()) {
            case "LAGRANGE" ->
                    resultado = domainService.lagrange(
                            request.prazos(),
                            request.taxas(),
                            request.prazoDesejado()
                    );

            case "NEWTON" ->
                    resultado = domainService.newton(
                            request.prazos(),
                            request.taxas(),
                            request.prazoDesejado()
                    );

            default -> throw new IllegalArgumentException(
                    "Método inválido. Use LAGRANGE ou NEWTON."
            );
        }

        return new InterpolacaoResponse(
                metodo.trim().toUpperCase(),
                request.prazoDesejado(),
                resultado
        );
    }
}

