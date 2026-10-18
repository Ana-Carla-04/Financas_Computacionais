package br.edu.ufersa.Financas_Computacionais.features.interpolacao.dto;
import br.edu.ufersa.Financas_Computacionais.features.interpolacao.TipoMetodoInterpolacao;

import java.util.List;

public record InterpolacaoRequest(
        List<Double> prazos,
        List<Double> taxas,
        double prazoDesejado,
        TipoMetodoInterpolacao metodo
) {
}
