
package br.edu.ufersa.Financas_Computacionais.metodosNumericos;

public class FatoracaoCholesky {

    // Decomposicao A = L * L^T
    public static double[][] decomposicaoCholesky(double[][] A) {
        validarMatriz(A);
        validarSimetria(A);

        int n = A.length;
        double[][] L = new double[n][n];

        // Percorre a matriz coluna por coluna
        for (int j = 0; j < n; j++) {

            // Calcula o elemento da diagonal L[j][j]
            double soma = 0.0;

            for (int k = 0; k < j; k++) {
                soma += L[j][k] * L[j][k];
            }

            double radicando = A[j][j] - soma;

            if (radicando <= 1e-12) {
                throw new IllegalArgumentException(
                        "A matriz nao e definida positiva "
                                + "ou o calculo e numericamente instavel."
                );
            }

            L[j][j] = Math.sqrt(radicando);

            // Calcula os elementos abaixo da diagonal
            for (int i = j + 1; i < n; i++) {
                soma = 0.0;

                for (int k = 0; k < j; k++) {
                    soma += L[i][k] * L[j][k];
                }

                L[i][j] = (A[i][j] - soma) / L[j][j];
            }
        }

        return L;
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

    // Resolve L^T x = y por substituicao regressiva
    public static double[] substituicaoRegressivaTransposta(
            double[][] L, double[] y) {

        validarSistema(L, y);

        int n = L.length;
        double[] x = new double[n];

        for (int i = n - 1; i >= 0; i--) {
            double soma = 0.0;

            for (int k = i + 1; k < n; k++) {
                soma += L[k][i] * x[k];
            }

            if (Math.abs(L[i][i]) < 1e-12) {
                throw new IllegalArgumentException(
                        "Elemento nulo na diagonal de L."
                );
            }

            x[i] = (y[i] - soma) / L[i][i];
        }

        return x;
    }

    // Resolve Ax = b usando a decomposicao de Cholesky
    public static double[] resolverSistema(double[][] A, double[] b) {
        validarSistema(A, b);

        double[][] L = decomposicaoCholesky(A);

        double[] y = substituicaoProgressiva(L, b);

        return substituicaoRegressivaTransposta(L, y);
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

            for (int j = 0; j < n; j++) {
                if (!Double.isFinite(A[i][j])) {
                    throw new IllegalArgumentException(
                            "A matriz contem valores invalidos."
                    );
                }
            }
        }
    }

    // Verifica se A e simetrica
    private static void validarSimetria(double[][] A) {
        int n = A.length;
        double tolerancia = 1e-10;

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (Math.abs(A[i][j] - A[j][i]) > tolerancia) {
                    throw new IllegalArgumentException(
                            "A matriz deve ser simetrica."
                    );
                }
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

        for (int i = 0; i < b.length; i++) {
            if (!Double.isFinite(b[i])) {
                throw new IllegalArgumentException(
                        "O vetor b contem valores invalidos."
                );
            }
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
                {4, 12, -16},
                {12, 37, -43},
                {-16, -43, 98}
        };

        double[] b = {-20, -43, 192};

        // Etapa 1: decomposicao de Cholesky
        double[][] L = decomposicaoCholesky(A);

        // Etapa 2: substituicao progressiva
        double[] y = substituicaoProgressiva(L, b);

        // Etapa 3: substituicao regressiva em L^T
        double[] x = substituicaoRegressivaTransposta(L, y);

        imprimirMatriz("Matriz L", L);
        imprimirVetor("Vetor y", y);
        imprimirVetor("Solucao x", x);
    }
}

