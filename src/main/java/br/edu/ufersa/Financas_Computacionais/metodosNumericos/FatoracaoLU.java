
package br.edu.ufersa.Financas_Computacionais.metodosNumericos;

public class FatoracaoLU {

    // Decomposicao A = LU pelo metodo de Doolittle
    public static double[][][] decomposicaoLU(double[][] A) {
        validarMatriz(A);

        int n = A.length;
        double[][] L = new double[n][n];
        double[][] U = new double[n][n];

        // Inicializa L com diagonal igual a 1
        // e U com zeros
        for (int i = 0; i < n; i++) {
            L[i][i] = 1.0;
        }

        for (int i = 0; i < n; i++) {

            // Calcula a linha i de U
            for (int j = i; j < n; j++) {
                double soma = 0.0;

                for (int k = 0; k < i; k++) {
                    soma += L[i][k] * U[k][j];
                }

                U[i][j] = A[i][j] - soma;
            }

            // Verifica se o pivo permite continuar
            if (Math.abs(U[i][i]) < 1e-12) {
                throw new IllegalArgumentException(
                        "Pivo nulo ou muito proximo de zero na posicao "
                                + (i + 1) + ". O metodo sem pivoteamento nao pode continuar."
                );
            }

            // Calcula a coluna i de L abaixo da diagonal
            for (int j = i + 1; j < n; j++) {
                double soma = 0.0;

                for (int k = 0; k < i; k++) {
                    soma += L[j][k] * U[k][i];
                }

                L[j][i] = (A[j][i] - soma) / U[i][i];
            }
        }

        // Retorna L e U:
        // resultado[0] = L
        // resultado[1] = U
        return new double[][][] { L, U };
    }

    // Resolve Ly = b por substituicao progressiva
    public static double[] substituicaoProgressiva(
            double[][] L, double[] b) {

        validarSistema(L, b);

        int n = L.length;
        double[] y = new double[n];

        for (int i = 0; i < n; i++) {
            double soma = 0.0;

            for (int k = 0; k < i; k++) {
                soma += L[i][k] * y[k];
            }

            if (Math.abs(L[i][i]) < 1e-12) {
                throw new IllegalArgumentException(
                        "Elemento nulo na diagonal de L."
                );
            }

            y[i] = (b[i] - soma) / L[i][i];
        }

        return y;
    }

    // Resolve Ux = y por substituicao regressiva
    public static double[] substituicaoRegressiva(
            double[][] U, double[] y) {

        validarSistema(U, y);

        int n = U.length;
        double[] x = new double[n];

        for (int i = n - 1; i >= 0; i--) {
            double soma = 0.0;

            for (int k = i + 1; k < n; k++) {
                soma += U[i][k] * x[k];
            }

            if (Math.abs(U[i][i]) < 1e-12) {
                throw new IllegalArgumentException(
                        "Elemento nulo na diagonal de U."
                );
            }

            x[i] = (y[i] - soma) / U[i][i];
        }

        return x;
    }

    // Resolve Ax = b usando as tres etapas anteriores
    public static double[] resolverSistema(double[][] A, double[] b) {
        validarSistema(A, b);

        double[][][] fatores = decomposicaoLU(A);
        double[][] L = fatores[0];
        double[][] U = fatores[1];

        double[] y = substituicaoProgressiva(L, b);
        return substituicaoRegressiva(U, y);
    }

    // Verifica se a matriz e quadrada e nao esta vazia
    private static void validarMatriz(double[][] A) {
        if (A == null || A.length == 0) {
            throw new IllegalArgumentException(
                    "A matriz nao pode ser vazia."
            );
        }

        int n = A.length;

        for (int i = 0; i < n; i++) {
            if (A[i] == null || A[i].length != n) {
                throw new IllegalArgumentException(
                        "A matriz deve ser quadrada."
                );
            }
        }
    }

    // Verifica se o vetor tem o mesmo tamanho da matriz
    private static void validarSistema(double[][] A, double[] b) {
        validarMatriz(A);

        if (b == null || b.length != A.length) {
            throw new IllegalArgumentException(
                    "O vetor b deve ter o mesmo tamanho da matriz."
            );
        }
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

    // Teste da implementacao
    public static void main(String[] args) {

        double[][] A = {
                {2, 3, 1},
                {4, 7, 3},
                {6, 18, 5}
        };

        double[] b = {11, 27, 57};

        // Etapa 1: decomposicao LU
        double[][][] fatores = decomposicaoLU(A);
        double[][] L = fatores[0];
        double[][] U = fatores[1];

        // Etapa 2: substituicao progressiva
        double[] y = substituicaoProgressiva(L, b);

        // Etapa 3: substituicao regressiva
        double[] x = substituicaoRegressiva(U, y);

        imprimirMatriz("Matriz L", L);
        imprimirMatriz("Matriz U", U);
        imprimirVetor("Vetor y", y);
        imprimirVetor("Solucao x", x);
    }
}
