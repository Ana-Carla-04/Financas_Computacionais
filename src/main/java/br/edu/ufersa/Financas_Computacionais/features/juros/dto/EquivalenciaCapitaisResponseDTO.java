package br.edu.ufersa.Financas_Computacionais.features.juros.dto;

// Resultado da equivalência de capitais.
//  - aplicacoes: valor a aplicar hoje em cada conta;
//  - fatoresCrescimento: matriz A, com A[m][j] = (1 + taxa_j)^prazo_m;
//  - residuo: erro máximo |A x - metas| da solução calculada.
public record EquivalenciaCapitaisResponseDTO(
        double[] aplicacoes,
        double[][] fatoresCrescimento,
        double numeroCondicionamento,
        String classificacao,
        int digitosPerdidos,
        double residuo,
        String alerta
) {
}