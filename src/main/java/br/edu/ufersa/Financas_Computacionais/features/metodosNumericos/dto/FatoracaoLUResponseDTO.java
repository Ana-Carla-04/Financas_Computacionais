package br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.dto;

// Resultado da decomposição A = LU.
// Substitui o double[][][] que a versão antiga devolvia, onde era preciso
// lembrar que a posição 0 era L e a posição 1 era U.
public record FatoracaoLUResponseDTO(double[][] L, double[][] U) {
}
