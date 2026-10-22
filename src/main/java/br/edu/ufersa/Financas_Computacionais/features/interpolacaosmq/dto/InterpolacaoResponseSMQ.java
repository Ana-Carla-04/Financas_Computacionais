package br.edu.ufersa.Financas_Computacionais.features.interpolacaosmq.dto;

import br.edu.ufersa.Financas_Computacionais.features.interpolacaosmq.TipoMetodoInterpolacao;

public record InterpolacaoResponseSMQ(
        TipoMetodoInterpolacao metodo,
        double valor,
        double resultado
) {
}

