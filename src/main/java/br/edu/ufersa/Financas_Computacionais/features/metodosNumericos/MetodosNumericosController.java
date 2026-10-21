package br.edu.ufersa.Financas_Computacionais.features.metodosNumericos;

import br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.dto.EliminacaoGaussResponseDTO;
import br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.dto.FatoracaoLUResponseDTO;
import br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.exception.PivoNuloException;
import br.edu.ufersa.Financas_Computacionais.shared.util.ImpressaoConsole;

// Porta de entrada da feature, como os Controllers do Sistema_Web: recebe a
// chamada, repassa para os Services e apresenta o resultado (aqui, no console).
// Os testes de demonstração que ficavam no main de cada fatoração estão aqui.
//
// Também reúne as demonstrações de eliminação gaussiana, condicionamento e
// juros, executadas em sequência pelo main.
public class MetodosNumericosController {

    private final FatoracaoLUService fatoracaoLUService;
    private final FatoracaoCholeskyService fatoracaoCholeskyService;
    private final JacobiService jacobiService;
    private final GaussSeidelService gaussSeidelService;

    public MetodosNumericosController(FatoracaoLUService fatoracaoLUService,
                                      FatoracaoCholeskyService fatoracaoCholeskyService) {
        this(fatoracaoLUService, fatoracaoCholeskyService,
                new JacobiService(), new GaussSeidelService());
    }

    public MetodosNumericosController(FatoracaoLUService fatoracaoLUService,
                                      FatoracaoCholeskyService fatoracaoCholeskyService,
                                      JacobiService jacobiService,
                                      GaussSeidelService gaussSeidelService) {
        this.fatoracaoLUService = fatoracaoLUService;
        this.fatoracaoCholeskyService = fatoracaoCholeskyService;
        this.jacobiService = jacobiService;
        this.gaussSeidelService = gaussSeidelService;
    }

    public MetodosNumericosController() {
        this(new FatoracaoLUService(), new FatoracaoCholeskyService());
    }

    // Teste da implementacao da fatoracao LU
    public void demonstrarFatoracaoLU() {

        double[][] A = {
                {2, 3, 1},
                {4, 7, 3},
                {6, 18, 5}
        };

        double[] b = {11, 27, 57};

        // Etapa 1: decomposicao LU
        FatoracaoLUResponseDTO fatores = fatoracaoLUService.decomposicaoLU(A);
        double[][] L = fatores.L();
        double[][] U = fatores.U();

        // Etapa 2: substituicao progressiva
        double[] y = fatoracaoLUService.substituicaoProgressiva(L, b);

        // Etapa 3: substituicao regressiva
        double[] x = fatoracaoLUService.substituicaoRegressiva(U, y);

        ImpressaoConsole.imprimirMatriz("Matriz L", L);
        ImpressaoConsole.imprimirMatriz("Matriz U", U);
        ImpressaoConsole.imprimirVetor("Vetor y", y);
        ImpressaoConsole.imprimirVetor("Solucao x", x);
    }

    // Teste da implementacao da fatoracao de Cholesky
    public void demonstrarFatoracaoCholesky() {

        double[][] A = {
                {4, 12, -16},
                {12, 37, -43},
                {-16, -43, 98}
        };

        double[] b = {-20, -43, 192};

        // Etapa 1: decomposicao de Cholesky
        double[][] L = fatoracaoCholeskyService.decomposicaoCholesky(A);

        // Etapa 2: substituicao progressiva
        double[] y = fatoracaoCholeskyService.substituicaoProgressiva(L, b);

        // Etapa 3: substituicao regressiva em L^T
        double[] x = fatoracaoCholeskyService.substituicaoRegressivaTransposta(L, y);

        ImpressaoConsole.imprimirMatriz("Matriz L", L);
        ImpressaoConsole.imprimirVetor("Vetor y", y);
        ImpressaoConsole.imprimirVetor("Solucao x", x);
    }

    public void demonstrarJacobi() {
        double[][] A = {
                {10, -1, 2},
                {-1, 11, -1},
                {2, -1, 10}
        };
        double[] b = {6, 22, -10};
        double[] x = jacobiService.resolverSistema(A, b);

        ImpressaoConsole.imprimirVetor("Solucao por Jacobi", x);
    }

    public void demonstrarGaussSeidel() {
        double[][] A = {
                {10, -1, 2},
                {-1, 11, -1},
                {2, -1, 10}
        };
        double[] b = {6, 22, -10};
        double[] x = gaussSeidelService.resolverSistema(A, b);

        ImpressaoConsole.imprimirVetor("Solucao por Gauss-Seidel", x);
    }

    public static void main(String[] args) {
        MetodosNumericosController controller = new MetodosNumericosController();

        controller.demonstrarFatoracaoLU();
        System.out.println();
        controller.demonstrarFatoracaoCholesky();
        System.out.println();
        controller.demonstrarJacobi();
        System.out.println();
        controller.demonstrarGaussSeidel();
        System.out.println();
        controller.demonstrarEliminacaoGauss();
        System.out.println();
        controller.demonstrarCondicionamento();
        System.out.println();
        controller.demonstrarJuros();
    }

    // Teste da eliminacao gaussiana, sem e com pivoteamento parcial
    public void demonstrarEliminacaoGauss() {
        EliminacaoGaussService gauss = new EliminacaoGaussService();

        System.out.println("=== Gauss sem pivoteamento ===");
        double[][] A1 = {{2, 1, -1}, {-3, -1, 2}, {-2, 1, 2}};
        double[] b1 = {8, -11, -3};
        EliminacaoGaussResponseDTO r1 = gauss.resolver(A1, b1, false);
        ImpressaoConsole.imprimirMatriz("[U | y]", r1.matrizTriangular());
        ImpressaoConsole.imprimirVetor("Solucao x", r1.solucao());
        System.out.printf("Determinante: %.4f%n", r1.determinante());

        System.out.println();
        System.out.println("=== Sistema com a11 = 0 ===");
        double[][] A2 = {{0, 1, 1}, {2, 1, -1}, {1, 3, -1}};
        double[] b2 = {5, 1, 4};
        try {
            gauss.resolver(A2, b2, false);
        } catch (PivoNuloException e) {
            System.out.println("Sem pivoteamento: " + e.getMessage());
        }

        EliminacaoGaussResponseDTO r2 = gauss.resolver(A2, b2, true);
        System.out.println("Com pivoteamento parcial (" + r2.trocas().size() + " trocas de linha):");
        ImpressaoConsole.imprimirMatriz("[U | y]", r2.matrizTriangular());
        ImpressaoConsole.imprimirVetor("Solucao x", r2.solucao());
        System.out.printf("Determinante: %.4f%n", r2.determinante());
    }

    public void demonstrarJuros() {
        new br.edu.ufersa.Financas_Computacionais.features.juros
                .JurosController().demonstrar();
    }

    public void demonstrarAmortizacao() {
        new br.edu.ufersa.Financas_Computacionais.features.amortizacao
                .SistemaAmortizacaoController().demonstrar();
    }

    public void demonstrarCondicionamento() {
        new CondicionamentoController().demonstrar();
    }
}