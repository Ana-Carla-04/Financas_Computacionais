package br.edu.ufersa.Financas_Computacionais.features.interpolacao.dto;
import br.edu.ufersa.Financas_Computacionais.features.interpolacao.TipoMetodoInterpolacao;

public record InterpolacaoResponse(
        TipoMetodoInterpolacao metodo,
        double prazoDesejado,
        double taxaEstimada
) {
}
