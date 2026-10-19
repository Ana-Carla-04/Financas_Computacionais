package br.edu.ufersa.Financas_Computacionais.features.interpolacao.dto;
import br.edu.ufersa.Financas_Computacionais.features.interpolacao.TipoMetodoInterpolacao;
import java.util.List;

// Responsável por receber a requisição http
// User passa uma lista de prazos, uma lista de taxas, o prazoDesejado (x) e o método de cálculo

public record InterpolacaoRequest(
        List<Double> prazos,
        List<Double> taxas,
        double prazoDesejado,
        TipoMetodoInterpolacao metodo
) {
}
