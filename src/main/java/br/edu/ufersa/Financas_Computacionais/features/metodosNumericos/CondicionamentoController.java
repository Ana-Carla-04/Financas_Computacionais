package br.edu.ufersa.Financas_Computacionais.features.metodosNumericos;

import br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.AnaliseEstabilidadeService.EfeitoDaEscala;
import br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.AnaliseEstabilidadeService.Sensibilidade;
import br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.dto.CondicionamentoNormasResponseDTO;
import br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.dto.CondicionamentoResponseDTO;
import br.edu.ufersa.Financas_Computacionais.shared.util.ImpressaoConsole;

/**
 * Porta de entrada da parte de condicionamento dentro da feature
 * metodosNumericos — mesmo padrão de PrecificacaoController e
 * MetodosNumericosController.
 */
public class CondicionamentoController {

    private final CondicionamentoService service;
    private final AnaliseEstabilidadeService analiseService;

    public CondicionamentoController() {
        this(new CondicionamentoService());
    }

    public CondicionamentoController(CondicionamentoService service) {
        this(service, new AnaliseEstabilidadeService());
    }

    public CondicionamentoController(CondicionamentoService service,
                                     AnaliseEstabilidadeService analiseService) {
        this.service = service;
        this.analiseService = analiseService;
    }

    public CondicionamentoResponseDTO calcular(double[][] A) {
        return service.calcular(A);
    }

    public void demonstrar() {
        // Matriz "quase singular" do material da disciplina:
        //   [[1, 1], [1, 1.0001]] com det = 1e-4.
        double[][] mal = {
                {1.0, 1.0},
                {1.0, 1.0001}
        };
        double[][] bem = {
                {2.0, 1.0},
                {1.0, 3.0}
        };

        imprimirCondicionamento("Matriz quase singular", mal);
        System.out.println();
        imprimirCondicionamento("Matriz bem condicionada", bem);

        System.out.println();
        System.out.println("=== Sensibilidade: b = (2; 2,0001) e db = (0; 0,0001) ===");
        Sensibilidade s = analiseService.analisarPerturbacao(
                mal, new double[]{2.0, 2.0001}, new double[]{0.0, 0.0001});
        ImpressaoConsole.imprimirVetor("x", s.solucao());
        ImpressaoConsole.imprimirVetor("x perturbado", s.solucaoPerturbada());
        System.out.printf("Erro relativo em b: %.4e%n", s.erroRelativoDados());
        System.out.printf("Erro relativo em x: %.4f%n", s.erroRelativoSolucao());
        System.out.printf("Limitante kappa * erro em b: %.4f (respeitado: %s)%n",
                s.limitante(), s.limitanteRespeitado());

        System.out.println();
        System.out.println("=== Determinante nao e medida de condicionamento (A' = 0,001 A) ===");
        EfeitoDaEscala e = analiseService.compararEscala(mal, 0.001);
        System.out.printf("det(A) = %.4e | det(A') = %.4e%n",
                e.determinanteOriginal(), e.determinanteEscalado());
        System.out.printf("kappa(A) = %.4f | kappa(A') = %.4f%n",
                e.kappaOriginal(), e.kappaEscalado());
    }

    private void imprimirCondicionamento(String titulo, double[][] A) {
        System.out.println("=== " + titulo + " ===");
        for (TipoNorma norma : TipoNorma.values()) {
            CondicionamentoNormasResponseDTO r = service.calcular(A, norma);
            System.out.printf("[%s] ||A|| = %.4f | ||A^-1|| = %.4f | kappa = %.4f | %s | digitos perdidos: %d%n",
                    norma, r.normaMatriz(), r.normaInversa(), r.numeroCondicionamento(),
                    r.classificacao(), r.digitosPerdidos());
        }
    }
}