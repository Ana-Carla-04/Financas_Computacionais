package br.edu.ufersa.Financas_Computacionais.shared.validation;

import br.edu.ufersa.Financas_Computacionais.shared.exception.MatrizInvalidaException;

// Validações de matrizes e sistemas lineares usadas pelas features de
// métodos numéricos. Antes, cada fatoração tinha a sua própria cópia.
//
// Existem duas variantes de propósito, para manter o comportamento original
// de cada método:
//  - validarQuadrada / validarSistema: só confere forma e tamanho
//    (usadas pela fatoração LU);
//  - validarQuadradaFinita / validarSistemaFinito: confere também se todos os
//    valores são finitos, sem NaN ou infinito (usadas pelo Cholesky).
public final class ValidadorMatriz {

    private ValidadorMatriz() {
    }

    // Verifica se a matriz e quadrada e nao esta vazia
    public static void validarQuadrada(double[][] A) {
        if (A == null || A.length == 0) {
            throw new MatrizInvalidaException(
                    "A matriz nao pode ser vazia."
            );
        }

        int n = A.length;

        for (int i = 0; i < n; i++) {
            if (A[i] == null || A[i].length != n) {
                throw new MatrizInvalidaException(
                        "A matriz deve ser quadrada."
                );
            }
        }
    }

    // Mesma verificacao de validarQuadrada, mas tambem recusa NaN e infinito
    public static void validarQuadradaFinita(double[][] A) {
        if (A == null || A.length == 0) {
            throw new MatrizInvalidaException(
                    "A matriz nao pode ser vazia."
            );
        }

        int n = A.length;

        for (int i = 0; i < n; i++) {
            if (A[i] == null || A[i].length != n) {
                throw new MatrizInvalidaException(
                        "A matriz deve ser quadrada."
                );
            }

            for (int j = 0; j < n; j++) {
                if (!Double.isFinite(A[i][j])) {
                    throw new MatrizInvalidaException(
                            "A matriz contem valores invalidos."
                    );
                }
            }
        }
    }

    // Verifica se o vetor tem o mesmo tamanho da matriz
    public static void validarSistema(double[][] A, double[] b) {
        validarQuadrada(A);
        validarTamanhoDoVetor(A, b);
    }

    // Mesma verificacao de validarSistema, mas tambem recusa NaN e infinito
    public static void validarSistemaFinito(double[][] A, double[] b) {
        validarQuadradaFinita(A);
        validarTamanhoDoVetor(A, b);

        for (int i = 0; i < b.length; i++) {
            if (!Double.isFinite(b[i])) {
                throw new MatrizInvalidaException(
                        "O vetor b contem valores invalidos."
                );
            }
        }
    }

    private static void validarTamanhoDoVetor(double[][] A, double[] b) {
        if (b == null || b.length != A.length) {
            throw new MatrizInvalidaException(
                    "O vetor b deve ter o mesmo tamanho da matriz."
            );
        }
    }
}
