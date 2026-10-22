package br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.dto;

import br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.TipoNorma;

// Número de condicionamento calculado na norma escolhida.
public record CondicionamentoNormasResponseDTO(
        TipoNorma norma,
        double normaMatriz,
        double normaInversa,
        double numeroCondicionamento,
        String classificacao,
        int digitosPerdidos
) {
}