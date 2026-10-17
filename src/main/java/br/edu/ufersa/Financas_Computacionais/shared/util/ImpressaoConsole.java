package br.edu.ufersa.Financas_Computacionais.shared.util;

// Impressão de matrizes e vetores no console. Estava duplicada, idêntica,
// em FatoracaoLU e FatoracaoCholesky.
public final class ImpressaoConsole {

    private ImpressaoConsole() {
    }

    // Imprime uma matriz
    public static void imprimirMatriz(String nome, double[][] M) {
        System.out.println(nome + ":");

        for (int i = 0; i < M.length; i++) {
            System.out.print("[ ");

            for (int j = 0; j < M[i].length; j++) {
                System.out.printf("%10.4f ", M[i][j]);
            }

            System.out.println("]");
        }
    }

    // Imprime um vetor
    public static void imprimirVetor(String nome, double[] v) {
        System.out.print(nome + ": [ ");

        for (int i = 0; i < v.length; i++) {
            System.out.printf("%.4f ", v[i]);
        }

        System.out.println("]");
    }
}
