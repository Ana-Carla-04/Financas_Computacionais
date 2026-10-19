package br.edu.ufersa.Financas_Computacionais.features.interpolacao.dto;
import br.edu.ufersa.Financas_Computacionais.features.interpolacao.TipoMetodoInterpolacao;

// devolve ao usuário as informações http

public record InterpolacaoResponse(
        TipoMetodoInterpolacao metodo,
        double prazoDesejado,
        double taxaEstimada
) {
}
