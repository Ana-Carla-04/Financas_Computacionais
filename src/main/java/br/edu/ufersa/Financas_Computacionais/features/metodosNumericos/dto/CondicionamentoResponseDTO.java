package br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.dto;

// Resultado do número de condicionamento na norma infinito.
public record CondicionamentoResponseDTO(
        double normaMatriz,
        double normaInversa,
        double numeroCondicionamento,
        String classificacao,
        int digitosPerdidos
) {
}