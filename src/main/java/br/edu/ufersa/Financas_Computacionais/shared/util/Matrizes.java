package br.edu.ufersa.Financas_Computacionais.shared.util;

// Operações básicas com matrizes e vetores.
public final class Matrizes {

    private Matrizes() {
    }

    public static double[][] copiar(double[][] A) {
        double[][] copia = new double[A.length][];
        for (int i = 0; i < A.length; i++) {
            copia[i] = A[i].clone();
        }
        return copia;
    }

    public static double[][] identidade(int n) {
        double[][] I = new double[n][n];
        for (int i = 0; i < n; i++) {
            I[i][i] = 1.0;
        }
        return I;
    }

    // Monta a matriz aumentada [A | b]
    public static double[][] aumentada(double[][] A, double[] b) {
        int n = A.length;
        double[][] aum = new double[n][n + 1];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                aum[i][j] = A[i][j];
            }
            aum[i][n] = b[i];
        }
        return aum;
    }

    // Produto matriz x vetor
    public static double[] multiplicar(double[][] A, double[] x) {
        double[] y = new double[A.length];
        for (int i = 0; i < A.length; i++) {
            double soma = 0.0;
            for (int j = 0; j < x.length; j++) {
                soma += A[i][j] * x[j];
            }
            y[i] = soma;
        }
        return y;
    }

    // Produto matriz x matriz
    public static double[][] multiplicar(double[][] A, double[][] B) {
        int n = A.length;
        int m = B[0].length;
        double[][] C = new double[n][m];
        for (int i = 0; i < n; i++) {
            for (int k = 0; k < B.length; k++) {
                for (int j = 0; j < m; j++) {
                    C[i][j] += A[i][k] * B[k][j];
                }
            }
        }
        return C;
    }

    public static double[][] transpor(double[][] A) {
        double[][] T = new double[A[0].length][A.length];
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < A[0].length; j++) {
                T[j][i] = A[i][j];
            }
        }
        return T;
    }

    public static void trocarLinhas(double[][] M, int i, int j) {
        double[] aux = M[i];
        M[i] = M[j];
        M[j] = aux;
    }

    // Maior módulo entre os elementos da matriz
    public static double maiorModulo(double[][] A) {
        double maior = 0.0;
        for (double[] linha : A) {
            for (double v : linha) {
                maior = Math.max(maior, Math.abs(v));
            }
        }
        return maior;
    }
}