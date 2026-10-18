package br.edu.ufersa.Financas_Computacionais.features.amortizacao.dto;

public record AnaliseCondicionamentoDTO(
        double kappa,
        String classificacao,
        int digitosPerdidos,
        String mensagem
) {
}