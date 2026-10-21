package br.edu.ufersa.Financas_Computacionais.features.juros.dto;

// Resultado de uma operação de juros.
//  - operacao: "VALOR_FUTURO" ou "VALOR_PRESENTE";
//  - resultado: o valor futuro ou o valor presente calculado;
//  - juros: diferença entre o valor futuro e o valor presente.
public record JurosResponseDTO(
        String operacao,
        String regime,
        double valorInformado,
        double taxa,
        double periodos,
        double resultado,
        double juros
) {
}