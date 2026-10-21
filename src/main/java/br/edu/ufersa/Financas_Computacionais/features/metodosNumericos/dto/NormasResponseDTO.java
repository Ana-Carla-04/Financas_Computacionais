package br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.dto;

// Normas matriciais usuais de uma matriz.
public record NormasResponseDTO(
        double norma1,
        double normaInfinito,
        double normaFrobenius
) {
}