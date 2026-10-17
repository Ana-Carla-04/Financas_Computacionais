package br.edu.ufersa.Financas_Computacionais.features.metodosNumericos;

import br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.exception.MatrizNaoDefinidaPositivaException;
import br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.exception.MatrizNaoSimetricaException;
import br.edu.ufersa.Financas_Computacionais.shared.exception.DiagonalNulaException;
import br.edu.ufersa.Financas_Computacionais.shared.util.SubstituicaoTriangular;
import br.edu.ufersa.Financas_Computacionais.shared.validation.ValidadorMatriz;

public class FatoracaoCholeskyService {

    // Decomposicao A = L * L^T
    public double[][] decomposicaoCholesky(double[][] A) {
        ValidadorMatriz.validarQuadradaFinita(A);
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
                throw new MatrizNaoDefinidaPositivaException();
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
    public double[] substituicaoProgressiva(double[][] L, double[] b) {
        ValidadorMatriz.validarSistemaFinito(L, b);

        return SubstituicaoTriangular.progressiva(L, b);
    }

    // Resolve L^T x = y por substituicao regressiva
    public double[] substituicaoRegressivaTransposta(double[][] L, double[] y) {
        ValidadorMatriz.validarSistemaFinito(L, y);

        int n = L.length;
        double[] x = new double[n];

        for (int i = n - 1; i >= 0; i--) {
            double soma = 0.0;

            for (int k = i + 1; k < n; k++) {
                soma += L[k][i] * x[k];
            }

            if (Math.abs(L[i][i]) < 1e-12) {
                throw new DiagonalNulaException(
                        "Elemento nulo na diagonal de L."
                );
            }

            x[i] = (y[i] - soma) / L[i][i];
        }

        return x;
    }

    // Resolve Ax = b usando a decomposicao de Cholesky
    public double[] resolverSistema(double[][] A, double[] b) {
        ValidadorMatriz.validarSistemaFinito(A, b);

        double[][] L = decomposicaoCholesky(A);

        double[] y = substituicaoProgressiva(L, b);

        return substituicaoRegressivaTransposta(L, y);
    }

    // Verifica se A e simetrica
    private void validarSimetria(double[][] A) {
        int n = A.length;
        double tolerancia = 1e-10;

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (Math.abs(A[i][j] - A[j][i]) > tolerancia) {
                    throw new MatrizNaoSimetricaException();
                }
            }
        }
    }
}
