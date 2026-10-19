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
        if (request.prazos() == null || request.taxas() == null || request.prazos().isEmpty() || request.prazos().size() != request.taxas().size()) {
            throw new IllegalArgumentException(
                    "Informe a mesma quantidade de prazos e taxas."
            );
        } //se qualquer um dos dados for null ou quantidades desiguais de prazo e taxa

        if (request.prazos().size() < 2) {
            throw new IllegalArgumentException ("Informe pelo menos dois pontos.");
        } //Quantidade mínima de pontos

        for (int i = 0; i < request.prazos().size(); i++) {
            if (!Double.isFinite(request.prazos().get(i)) || !Double.isFinite(request.taxas().get(i))) {
                throw new IllegalArgumentException(
                        "Os prazos e as taxas devem ser números finitos."
                );
            } //Verifica se são números finitos

            for (int j = 0; j < i; j++) {
                if (request.prazos().get(i).equals(request.prazos().get(j))) {
                    throw new IllegalArgumentException("Os prazos devem ser distintos.");
                }
            } //Verifica s eos prazos são distintos
        }

        TipoMetodoInterpolacao metodo = request.metodo(); //Var com o método escolhido

        if (metodo == null) {throw new IllegalArgumentException ( "Informe LAGRANGE ou NEWTON.");
        } // Não aceita método null

        double resultado = switch (metodo) {
            // Caso seja LAGRANGE, ele chama o método lagrange do domain service
            case LAGRANGE -> domainService.lagrange( request.prazos(), request.taxas(), request.prazoDesejado());

            // Caso seja NEWTON, ele chama o método newton do domain service
            case NEWTON -> domainService.newton(request.prazos(), request.taxas(), request.prazoDesejado());
        };

        return new InterpolacaoResponse(metodo, request.prazoDesejado(), resultado); //Retorna o resultado
    }
}

