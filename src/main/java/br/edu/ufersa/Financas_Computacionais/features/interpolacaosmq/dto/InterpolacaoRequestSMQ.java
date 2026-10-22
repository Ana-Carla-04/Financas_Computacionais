package br.edu.ufersa.Financas_Computacionais.features.interpolacaosmq.dto;

import br.edu.ufersa.Financas_Computacionais.features.interpolacaosmq.TipoMetodoInterpolacao;

public record InterpolacaoRequestSMQ(
        TipoMetodoInterpolacao metodo,
        double[] x,
        double[] y,
        double valor
) {
}

