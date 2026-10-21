package br.edu.ufersa.Financas_Computacionais.features.metodosNumericos;

import br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.dto.ClassificacaoSistemaResponseDTO;
import br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.dto.EliminacaoGaussResponseDTO;
import br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.dto.EliminacaoGaussResponseDTO.Multiplicador;
import br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.dto.EliminacaoGaussResponseDTO.Troca;
import br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.exception.MatrizSingularException;
import br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.exception.PivoNuloException;
import br.edu.ufersa.Financas_Computacionais.shared.util.Matrizes;
import br.edu.ufersa.Financas_Computacionais.shared.validation.ValidadorMatriz;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Eliminação gaussiana, com ou sem pivoteamento parcial.
 *
 * Fase 1: leva [A | b] a um sistema triangular superior [U | y].
 * Fase 2: substituição regressiva em U x = y.
 *
 * Sem pivoteamento usa-se sempre a_jj como pivô (falha se for nulo).
 * Com pivoteamento parcial, em cada etapa j troca-se a linha j pela linha
 * de maior |a_ij|, i >= j.
 *
 * A tolerância para considerar um pivô nulo é relativa ao maior elemento
 * de A, para não depender da escala da matriz.
 */
@Service
public class EliminacaoGaussService {

    private static final double TOLERANCIA_RELATIVA = 1e-12;

    public EliminacaoGaussResponseDTO resolver(double[][] A, double[] b, boolean pivoteamento) {
        ValidadorMatriz.validarSistemaFinito(A, b);

        int n = A.length;
        double[][] aum = Matrizes.aumentada(A, b);
        double eps = tolerancia(A);

        List<Troca> trocas = new ArrayList<>();
        List<Multiplicador> multiplicadores = new ArrayList<>();

        // Fase de eliminação
        for (int j = 0; j < n; j++) {
            if (pivoteamento) {
                int p = linhaDoMaiorPivo(aum, j, n);
                if (p != j) {
                    Matrizes.trocarLinhas(aum, j, p);
                    trocas.add(new Troca(j + 1, j + 1, p + 1));
                }
            }

            if (Math.abs(aum[j][j]) <= eps) {
                if (pivoteamento) {
                    throw new MatrizSingularException();
                }
                throw new PivoNuloException(j + 1);
            }

            for (int i = j + 1; i < n; i++) {
                double m = aum[i][j] / aum[j][j];
                multiplicadores.add(new Multiplicador(i + 1, j + 1, m));
                for (int k = j; k <= n; k++) {
                    aum[i][k] -= m * aum[j][k];
                }
                aum[i][j] = 0.0;
            }
        }

        // Fase de substituição regressiva
        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            double soma = 0.0;
            for (int k = i + 1; k < n; k++) {
                soma += aum[i][k] * x[k];
            }
            x[i] = (aum[i][n] - soma) / aum[i][i];
        }

        double det = 1.0;
        for (int i = 0; i < n; i++) {
            det *= aum[i][i];
        }
        if (trocas.size() % 2 == 1) {
            det = -det;
        }

        return new EliminacaoGaussResponseDTO(x, aum, pivoteamento, trocas, multiplicadores, det);
    }

    // Determinante por eliminação com pivoteamento parcial (0 se singular).
    public double determinante(double[][] A) {
        ValidadorMatriz.validarQuadradaFinita(A);

        int n = A.length;
        double[][] M = Matrizes.copiar(A);
        double eps = tolerancia(A);
        double det = 1.0;

        for (int j = 0; j < n; j++) {
            int p = linhaDoMaiorPivo(M, j, n);
            if (Math.abs(M[p][j]) <= eps) {
                return 0.0;
            }
            if (p != j) {
                Matrizes.trocarLinhas(M, j, p);
                det = -det;
            }
            det *= M[j][j];
            for (int i = j + 1; i < n; i++) {
                double m = M[i][j] / M[j][j];
                for (int k = j; k < n; k++) {
                    M[i][k] -= m * M[j][k];
                }
            }
        }
        return det;
    }

    // Inversa por eliminação em [A | I] com pivoteamento parcial.
    public double[][] inversa(double[][] A) {
        ValidadorMatriz.validarQuadradaFinita(A);

        int n = A.length;
        double eps = tolerancia(A);
        double[][] aum = new double[n][2 * n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                aum[i][j] = A[i][j];
            }
            aum[i][n + i] = 1.0;
        }

        for (int j = 0; j < n; j++) {
            int p = linhaDoMaiorPivo(aum, j, n);
            if (Math.abs(aum[p][j]) <= eps) {
                throw new MatrizSingularException();
            }
            Matrizes.trocarLinhas(aum, j, p);

            for (int i = j + 1; i < n; i++) {
                double m = aum[i][j] / aum[j][j];
                for (int k = j; k < 2 * n; k++) {
                    aum[i][k] -= m * aum[j][k];
                }
                aum[i][j] = 0.0;
            }
        }

        double[][] inv = new double[n][n];
        for (int c = 0; c < n; c++) {
            for (int i = n - 1; i >= 0; i--) {
                double soma = 0.0;
                for (int k = i + 1; k < n; k++) {
                    soma += aum[i][k] * inv[k][c];
                }
                inv[i][c] = (aum[i][n + c] - soma) / aum[i][i];
            }
        }
        return inv;
    }

    // Classifica Ax = b em SPD, SPI ou SI a partir do posto de A e de [A | b].
    public ClassificacaoSistemaResponseDTO classificar(double[][] A, double[] b) {
        ValidadorMatriz.validarSistemaFinito(A, b);

        int n = A.length;
        double[][] aum = Matrizes.aumentada(A, b);
        double eps = tolerancia(A);
        int posto = 0;

        for (int j = 0; j < n && posto < n; j++) {
            int p = linhaDoMaiorPivo(aum, posto, j, n);
            if (Math.abs(aum[p][j]) <= eps) {
                continue;
            }
            Matrizes.trocarLinhas(aum, posto, p);
            for (int i = posto + 1; i < n; i++) {
                double m = aum[i][j] / aum[posto][j];
                for (int k = j; k <= n; k++) {
                    aum[i][k] -= m * aum[posto][k];
                }
            }
            posto++;
        }

        double det = determinante(A);

        if (posto == n) {
            return new ClassificacaoSistemaResponseDTO("SPD",
                    "Sistema possivel e determinado: solucao unica.", det, posto);
        }

        boolean inconsistente = false;
        double epsB = TOLERANCIA_RELATIVA * Math.max(1.0, Matrizes.maiorModulo(aum));
        for (int i = posto; i < n; i++) {
            if (Math.abs(aum[i][n]) > epsB) {
                inconsistente = true;
            }
        }

        if (inconsistente) {
            return new ClassificacaoSistemaResponseDTO("SI",
                    "Sistema impossivel: nenhuma solucao.", det, posto);
        }
        return new ClassificacaoSistemaResponseDTO("SPI",
                "Sistema possivel e indeterminado: infinitas solucoes.", det, posto);
    }

    private int linhaDoMaiorPivo(double[][] M, int coluna, int n) {
        return linhaDoMaiorPivo(M, coluna, coluna, n);
    }

    private int linhaDoMaiorPivo(double[][] M, int linhaInicial, int coluna, int n) {
        int p = linhaInicial;
        for (int i = linhaInicial + 1; i < n; i++) {
            if (Math.abs(M[i][coluna]) > Math.abs(M[p][coluna])) {
                p = i;
            }
        }
        return p;
    }

    private double tolerancia(double[][] A) {
        return TOLERANCIA_RELATIVA * Matrizes.maiorModulo(A);
    }
}