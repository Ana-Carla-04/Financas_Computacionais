package br.edu.ufersa.Financas_Computacionais.features.amortizacao.dto;

import java.util.List;

public record TabelaAmortizacaoDTO(
        String sistema,
        double parcelaInicial,
        double parcelaFinal,
        double totalJuros,
        double totalPago,
        List<LinhaAmortizacao> linhas,
        AnaliseCondicionamentoDTO condicionamento
) {
    public record LinhaAmortizacao(
            int mes,
            double saldoInicial,
            double juros,
            double amortizacao,
            double parcela,
            double saldoFinal
    ) {
    }
}