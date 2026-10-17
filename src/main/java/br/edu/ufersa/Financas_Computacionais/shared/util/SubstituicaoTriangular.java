package br.edu.ufersa.Financas_Computacionais.shared.util;

import br.edu.ufersa.Financas_Computacionais.shared.exception.DiagonalNulaException;

// Substituição progressiva (Ly = b), que era idêntica na fatoração LU e no
// Cholesky. Não valida o formato de L nem de b: quem chama valida antes
// (cada fatoração com a sua regra, ver ValidadorMatriz).
public final class SubstituicaoTriangular {

    private SubstituicaoTriangular() {
    }

    // Resolve Ly = b por substituicao progressiva
    public static double[] progressiva(double[][] L, double[] b) {
        int n = L.length;
        double[] y = new double[n];

        for (int i = 0; i < n; i++) {
            double soma = 0.0;

            for (int k = 0; k < i; k++) {
                soma += L[i][k] * y[k];
            }

            if (Math.abs(L[i][i]) < 1e-12) {
                throw new DiagonalNulaException(
                        "Elemento nulo na diagonal de L."
                );
            }

            y[i] = (b[i] - soma) / L[i][i];
        }

        return y;
    }
}
