package br.edu.ufersa.Financas_Computacionais.features.juros;

import br.edu.ufersa.Financas_Computacionais.features.juros.dto.EquivalenciaCapitaisRequestDTO;
import br.edu.ufersa.Financas_Computacionais.features.juros.dto.EquivalenciaCapitaisResponseDTO;
import br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.CondicionamentoService;
import br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.EliminacaoGaussService;
import br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.TipoNorma;
import br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.dto.CondicionamentoNormasResponseDTO;
import br.edu.ufersa.Financas_Computacionais.shared.util.Matrizes;
import br.edu.ufersa.Financas_Computacionais.shared.util.Normas;
import org.springframework.stereotype.Service;

/**
 * Equivalência de capitais como sistema linear.
 *
 * Aplicamos x_j hoje na conta j (juros compostos, taxa i_j). Para ter a meta
 * V_m na data T_m, precisamos de   sum_j x_j (1 + i_j)^T_m = V_m,  ou seja,
 * A x = V com A[m][j] = (1 + i_j)^T_m. O sistema é resolvido por eliminação
 * gaussiana com pivoteamento parcial, e o número de condicionamento mostra
 * o quanto o resultado é sensível: taxas muito parecidas deixam as colunas de
 * A quase iguais, a matriz fica quase singular e kappa explode.
 */
@Service
public class EquivalenciaCapitaisService {

    private final EliminacaoGaussService gaussService;
    private final CondicionamentoService condicionamentoService;

    public EquivalenciaCapitaisService() {
        this(new EliminacaoGaussService(), new CondicionamentoService());
    }

    public EquivalenciaCapitaisService(EliminacaoGaussService gaussService,
                                       CondicionamentoService condicionamentoService) {
        this.gaussService = gaussService;
        this.condicionamentoService = condicionamentoService;
    }

    public EquivalenciaCapitaisResponseDTO calcular(EquivalenciaCapitaisRequestDTO request) {
        return calcular(request.taxas(), request.prazos(), request.metas());
    }

    public EquivalenciaCapitaisResponseDTO calcular(double[] taxas, double[] prazos, double[] metas) {
        int n = taxas.length;
        double[][] A = new double[n][n];
        for (int m = 0; m < n; m++) {
            for (int j = 0; j < n; j++) {
                A[m][j] = Math.pow(1.0 + taxas[j], prazos[m]);
            }
        }

        double[] x = gaussService.resolver(A, metas, true).solucao();
        CondicionamentoNormasResponseDTO cond =
                condicionamentoService.calcular(A, TipoNorma.INFINITO);

        double[] ax = Matrizes.multiplicar(A, x);
        double[] diferenca = new double[n];
        for (int i = 0; i < n; i++) {
            diferenca[i] = ax[i] - metas[i];
        }

        return new EquivalenciaCapitaisResponseDTO(
                x,
                A,
                cond.numeroCondicionamento(),
                cond.classificacao(),
                cond.digitosPerdidos(),
                Normas.vetorialInfinito(diferenca),
                montarAlerta(cond.numeroCondicionamento())
        );
    }

    private String montarAlerta(double kappa) {
        if (kappa >= 1e3) {
            return "Taxas e prazos muito parecidos: as aplicacoes calculadas sao muito "
                    + "sensiveis a pequenas variacoes nas metas.";
        }
        return "Sistema bem comportado: pequenas variacoes nas metas geram "
                + "pequenas variacoes nas aplicacoes.";
    }
}