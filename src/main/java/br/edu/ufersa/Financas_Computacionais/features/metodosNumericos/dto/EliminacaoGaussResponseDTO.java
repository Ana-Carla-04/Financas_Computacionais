package br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.dto;

import java.util.List;

// Resultado da eliminação gaussiana: solução, sistema triangular [U | y]
// e o registro de tudo o que aconteceu durante a eliminação.
public record EliminacaoGaussResponseDTO(
        double[] solucao,
        double[][] matrizTriangular,
        boolean pivoteamento,
        List<Troca> trocas,
        List<Multiplicador> multiplicadores,
        double determinante
) {

    // Troca de linhas feita na etapa indicada (numeração a partir de 1).
    public record Troca(int etapa, int linhaOrigem, int linhaDestino) {
    }

    // Multiplicador m = a_ij / a_jj usado para zerar a posição (linha, coluna).
    public record Multiplicador(int linha, int coluna, double valor) {
    }
}