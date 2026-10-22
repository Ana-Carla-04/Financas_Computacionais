package br.edu.ufersa.Financas_Computacionais.features.metodosNumericos;

import br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.dto.CondicionamentoNormasResponseDTO;
import br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.dto.CondicionamentoResponseDTO;
import br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.exception.MatrizSingularException;
import br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.exception.PivoNuloException;
import br.edu.ufersa.Financas_Computacionais.shared.util.Normas;
import br.edu.ufersa.Financas_Computacionais.shared.validation.ValidadorMatriz;
import org.springframework.stereotype.Service;

/**
 * Numero de condicionamento na norma infinito:
 *
 *   ||A||_inf = max_i (soma_j |a_ij|)
 *   kappa_inf(A) = ||A||_inf * ||A^-1||_inf
 *
 * A inversa é obtida resolvendo A*X = I coluna a coluna com a
 * fatoração LU já existente (Doolittle), reaproveitando os fatores.
 *
 * Extensão: calcular(A, norma) também permite as normas 1 e Frobenius,
 * obtendo a inversa por eliminação gaussiana com pivoteamento parcial
 * (EliminacaoGaussService). calcular(A) continua usando a norma infinito
 * e LU, pois é o que a amortização utiliza.
 *
 */
@Service
public class CondicionamentoService {

    private final FatoracaoLUService fatoracaoLUService;
    private final EliminacaoGaussService eliminacaoGaussService;

    public CondicionamentoService() {
        this(new FatoracaoLUService(), new EliminacaoGaussService());
    }

    public CondicionamentoService(FatoracaoLUService fatoracaoLUService) {
        this(fatoracaoLUService, new EliminacaoGaussService());
    }

    public CondicionamentoService(FatoracaoLUService fatoracaoLUService,
                                  EliminacaoGaussService eliminacaoGaussService) {
        this.fatoracaoLUService = fatoracaoLUService;
        this.eliminacaoGaussService = eliminacaoGaussService;
    }

    public double normaInfinito(double[] vetor) {
        double maior = 0.0;
        for (double v : vetor) {
            maior = Math.max(maior, Math.abs(v));
        }
        return maior;
    }

    public double normaInfinito(double[][] matriz) {
        int n = matriz.length;
        double maior = 0.0;
        for (int i = 0; i < n; i++) {
            double soma = 0.0;
            for (int j = 0; j < n; j++) {
                soma += Math.abs(matriz[i][j]);
            }
            maior = Math.max(maior, soma);
        }
        return maior;
    }

    /** Inverte A resolvendo A*X = I coluna a coluna, reaproveitando LU. */
    public double[][] inversa(double[][] A) {
        ValidadorMatriz.validarQuadrada(A);

        int n = A.length;
        double[][] inv = new double[n][n];

        try {
            var fatores = fatoracaoLUService.decomposicaoLU(A);
            double[][] L = fatores.L();
            double[][] U = fatores.U();

            for (int col = 0; col < n; col++) {
                double[] e = new double[n];
                e[col] = 1.0;

                double[] y = fatoracaoLUService.substituicaoProgressiva(L, e);
                double[] x = fatoracaoLUService.substituicaoRegressiva(U, y);

                for (int lin = 0; lin < n; lin++) {
                    inv[lin][col] = x[lin];
                }
            }
        } catch (PivoNuloException ex) {
            throw new MatrizSingularException();
        }

        return inv;
    }

    public CondicionamentoResponseDTO calcular(double[][] A) {
        ValidadorMatriz.validarQuadradaFinita(A);

        double normaA = normaInfinito(A);
        double[][] inversa = inversa(A);
        double normaInv = normaInfinito(inversa);

        double kappa = normaA * normaInv;

        if (!Double.isFinite(kappa)) {
            throw new MatrizSingularException();
        }

        int digitosPerdidos = (int) Math.floor(Math.log10(Math.max(kappa, 1.0)));

        return new CondicionamentoResponseDTO(
                normaA,
                normaInv,
                kappa,
                classificar(kappa),
                digitosPerdidos
        );
    }

    /**
     * kappa(A) = ||A|| * ||A^-1|| na norma escolhida (1, infinito ou Frobenius).
     * A inversa vem da eliminação gaussiana com pivoteamento parcial.
     */
    public CondicionamentoNormasResponseDTO calcular(double[][] A, TipoNorma norma) {
        ValidadorMatriz.validarQuadradaFinita(A);

        double normaA = normaMatriz(A, norma);
        double normaInv = normaMatriz(eliminacaoGaussService.inversa(A), norma);
        double kappa = normaA * normaInv;

        if (!Double.isFinite(kappa)) {
            throw new MatrizSingularException();
        }

        int digitosPerdidos = (int) Math.floor(Math.log10(Math.max(kappa, 1.0)));

        return new CondicionamentoNormasResponseDTO(
                norma,
                normaA,
                normaInv,
                kappa,
                classificar(kappa),
                digitosPerdidos
        );
    }

    private double normaMatriz(double[][] M, TipoNorma norma) {
        return switch (norma) {
            case UM -> Normas.matricial1(M);
            case INFINITO -> Normas.matricialInfinito(M);
            case FROBENIUS -> Normas.matricialFrobenius(M);
        };
    }

    private String classificar(double kappa) {
        if (kappa < 10) {
            return "Bem condicionada";
        } else if (kappa < 1e3) {
            return "Aceitavel";
        } else if (kappa < 1e6) {
            return "Mal condicionada (atencao)";
        } else {
            return "Severamente mal condicionada";
        }
    }
}