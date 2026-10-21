package br.edu.ufersa.Financas_Computacionais.features.metodosNumericos;

import br.edu.ufersa.Financas_Computacionais.shared.exception.MatrizInvalidaException;
import br.edu.ufersa.Financas_Computacionais.shared.util.Matrizes;
import br.edu.ufersa.Financas_Computacionais.shared.util.Normas;
import br.edu.ufersa.Financas_Computacionais.shared.validation.ValidadorMatriz;
import org.springframework.stereotype.Service;

/**
 * Estabilidade de sistemas lineares: o quanto um erro nos dados altera a solução.
 *
 *   ||dx|| / ||x||  <=  kappa(A) * ||db|| / ||b||
 *
 * Tudo na norma infinito.
 */
@Service
public class AnaliseEstabilidadeService {

    // Resultado do experimento com b -> b + db.
    public record Sensibilidade(
            double[] solucao,
            double[] solucaoPerturbada,
            double erroRelativoDados,
            double erroRelativoSolucao,
            double kappa,
            double limitante,
            boolean limitanteRespeitado
    ) {
    }

    // Efeito de multiplicar a matriz por um escalar: o determinante muda, kappa não.
    public record EfeitoDaEscala(
            double determinanteOriginal,
            double determinanteEscalado,
            double kappaOriginal,
            double kappaEscalado
    ) {
    }

    private final EliminacaoGaussService gaussService;
    private final CondicionamentoService condicionamentoService;

    public AnaliseEstabilidadeService() {
        this(new EliminacaoGaussService(), new CondicionamentoService());
    }

    public AnaliseEstabilidadeService(EliminacaoGaussService gaussService,
                                      CondicionamentoService condicionamentoService) {
        this.gaussService = gaussService;
        this.condicionamentoService = condicionamentoService;
    }

    public Sensibilidade analisarPerturbacao(double[][] A, double[] b, double[] deltaB) {
        ValidadorMatriz.validarSistemaFinito(A, b);
        if (deltaB == null || deltaB.length != b.length) {
            throw new MatrizInvalidaException("A perturbacao deve ter o mesmo tamanho de b.");
        }
        for (double v : deltaB) {
            if (!Double.isFinite(v)) {
                throw new MatrizInvalidaException("A perturbacao contem valores invalidos.");
            }
        }

        double normaB = Normas.vetorialInfinito(b);
        if (normaB == 0.0) {
            throw new MatrizInvalidaException("O vetor b nao pode ser nulo.");
        }

        double[] bPerturbado = new double[b.length];
        for (int i = 0; i < b.length; i++) {
            bPerturbado[i] = b[i] + deltaB[i];
        }

        double[] x = gaussService.resolver(A, b, true).solucao();
        double[] xPerturbado = gaussService.resolver(A, bPerturbado, true).solucao();

        double[] dx = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            dx[i] = xPerturbado[i] - x[i];
        }

        double erroDados = Normas.vetorialInfinito(deltaB) / normaB;
        double erroSolucao = Normas.vetorialInfinito(dx) / Normas.vetorialInfinito(x);
        double kappa = condicionamentoService.calcular(A, TipoNorma.INFINITO).numeroCondicionamento();
        double limitante = kappa * erroDados;

        // pequena folga para erros de arredondamento
        boolean respeitado = erroSolucao <= limitante * (1.0 + 1e-9) + 1e-15;

        return new Sensibilidade(x, xPerturbado, erroDados, erroSolucao, kappa, limitante, respeitado);
    }

    public EfeitoDaEscala compararEscala(double[][] A, double alfa) {
        ValidadorMatriz.validarQuadradaFinita(A);
        if (!Double.isFinite(alfa) || alfa == 0.0) {
            throw new MatrizInvalidaException("O escalar deve ser finito e diferente de zero.");
        }

        double[][] escalada = Matrizes.copiar(A);
        for (double[] linha : escalada) {
            for (int j = 0; j < linha.length; j++) {
                linha[j] *= alfa;
            }
        }

        return new EfeitoDaEscala(
                gaussService.determinante(A),
                gaussService.determinante(escalada),
                condicionamentoService.calcular(A, TipoNorma.INFINITO).numeroCondicionamento(),
                condicionamentoService.calcular(escalada, TipoNorma.INFINITO).numeroCondicionamento()
        );
    }
}