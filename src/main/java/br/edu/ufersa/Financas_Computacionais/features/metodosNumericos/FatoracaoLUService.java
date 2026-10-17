package br.edu.ufersa.Financas_Computacionais.features.metodosNumericos;

import br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.dto.FatoracaoLUResponseDTO;
import br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.exception.PivoNuloException;
import br.edu.ufersa.Financas_Computacionais.shared.exception.DiagonalNulaException;
import br.edu.ufersa.Financas_Computacionais.shared.util.SubstituicaoTriangular;
import br.edu.ufersa.Financas_Computacionais.shared.validation.ValidadorMatriz;

public class FatoracaoLUService {

    // Decomposicao A = LU pelo metodo de Doolittle
    public FatoracaoLUResponseDTO decomposicaoLU(double[][] A) {
        ValidadorMatriz.validarQuadrada(A);

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
                throw new PivoNuloException(i + 1);
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

        return new FatoracaoLUResponseDTO(L, U);
    }

    // Resolve Ly = b por substituicao progressiva
    public double[] substituicaoProgressiva(double[][] L, double[] b) {
        ValidadorMatriz.validarSistema(L, b);

        return SubstituicaoTriangular.progressiva(L, b);
    }

    // Resolve Ux = y por substituicao regressiva
    public double[] substituicaoRegressiva(double[][] U, double[] y) {
        ValidadorMatriz.validarSistema(U, y);

        int n = U.length;
        double[] x = new double[n];

        for (int i = n - 1; i >= 0; i--) {
            double soma = 0.0;

            for (int k = i + 1; k < n; k++) {
                soma += U[i][k] * x[k];
            }

            if (Math.abs(U[i][i]) < 1e-12) {
                throw new DiagonalNulaException(
                        "Elemento nulo na diagonal de U."
                );
            }

            x[i] = (y[i] - soma) / U[i][i];
        }

        return x;
    }

    // Resolve Ax = b usando as tres etapas anteriores
    public double[] resolverSistema(double[][] A, double[] b) {
        ValidadorMatriz.validarSistema(A, b);

        FatoracaoLUResponseDTO fatores = decomposicaoLU(A);

        double[] y = substituicaoProgressiva(fatores.L(), b);
        return substituicaoRegressiva(fatores.U(), y);
    }
}
