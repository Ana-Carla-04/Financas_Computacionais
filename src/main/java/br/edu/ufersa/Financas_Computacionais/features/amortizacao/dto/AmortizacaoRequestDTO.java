package br.edu.ufersa.Financas_Computacionais.features.amortizacao.dto;

public record AmortizacaoRequestDTO(
        String sistema,
        double valorFinanciado,
        double taxaJuros,
        int meses
) {
}