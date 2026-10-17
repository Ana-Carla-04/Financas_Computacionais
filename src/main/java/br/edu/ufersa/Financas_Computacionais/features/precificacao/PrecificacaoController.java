package br.edu.ufersa.Financas_Computacionais.features.precificacao;

import br.edu.ufersa.Financas_Computacionais.features.precificacao.dto.ArvoreBinomialRequestDTO;
import br.edu.ufersa.Financas_Computacionais.features.precificacao.dto.CoxRossRubinsteinRequestDTO;
import br.edu.ufersa.Financas_Computacionais.features.precificacao.dto.CoxRossRubinsteinResponseDTO;

// Porta de entrada da feature, como os Controllers do Sistema_Web: recebe a
// chamada, repassa para os Services e apresenta o resultado (aqui, no console).
// Os testes de demonstração que ficavam no main de ArvoreBinomial e de
// CoxRossRubinstein estão aqui.
public class PrecificacaoController {

    private final ArvoreBinomialService arvoreBinomialService;
    private final CoxRossRubinsteinService coxRossRubinsteinService;

    public PrecificacaoController(ArvoreBinomialService arvoreBinomialService,
                                  CoxRossRubinsteinService coxRossRubinsteinService) {
        this.arvoreBinomialService = arvoreBinomialService;
        this.coxRossRubinsteinService = coxRossRubinsteinService;
    }

    public PrecificacaoController() {
        this(new ArvoreBinomialService(), new CoxRossRubinsteinService());
    }

    /*
     * TESTE DA ARVORE BINOMIAL
     *
     * Os valores sao hipoteticos e servem para demonstrar
     * como utilizar os metodos.
     */
    public void demonstrarArvoreBinomial() {

        double S0 = 100.0; // preco atual do ativo: R$ 100
        double K = 100.0;  // preco de exercicio: R$ 100
        double r = 0.05;   // taxa livre de risco: 5% ao ano
        double T = 1.0;    // vencimento em um ano
        int N = 2;         // dois periodos

        double u = 1.10;   // fator de alta: 10%
        double d = 0.90;   // fator de baixa: 10%

        // Calcula a probabilidade neutra ao risco.
        double p = arvoreBinomialService.calcularProbabilidade(r, T, N, u, d);

        // Constroi e exibe a arvore de precos do ativo.
        double[][] arvore = arvoreBinomialService.construirArvore(S0, u, d, N);
        imprimirArvore(arvore);

        // Calcula os precos das duas opcoes.
        ArvoreBinomialRequestDTO request =
                new ArvoreBinomialRequestDTO(S0, K, r, T, N, u, d, p);

        double call = arvoreBinomialService.precificarCall(request);

        double put = arvoreBinomialService.precificarPut(request);

        System.out.printf(
                "%nProbabilidade neutra ao risco: %.4f%n", p
        );

        System.out.printf(
                "Preco da CALL: R$ %.4f%n", call
        );

        System.out.printf(
                "Preco da PUT: R$ %.4f%n", put
        );
    }

    // Teste do modelo Cox-Ross-Rubinstein
    public void demonstrarCoxRossRubinstein() {

        CoxRossRubinsteinResponseDTO resultado = coxRossRubinsteinService.calcular(
                new CoxRossRubinsteinRequestDTO(
                        100.0,  // Preço atual do ativo
                        100.0,  // Preço de exercício
                        1.0,    // Tempo até o vencimento (anos)
                        0.05,   // Taxa de juros: 5% ao ano
                        0.20,   // Volatilidade: 20% ao ano
                        100     // Número de períodos
                )
        );

        System.out.println("Modelo Cox-Ross-Rubinstein");
        System.out.println("--------------------------");

        System.out.printf("Delta t: %.6f%n", resultado.deltaT());
        System.out.printf("Fator de subida: %.6f%n",
                resultado.subida());
        System.out.printf("Fator de descida: %.6f%n",
                resultado.descida());
        System.out.printf("Probabilidade de subida: %.6f%n",
                resultado.probabilidadeSubida());

        System.out.printf("Preço da opção Call: %.2f%n",
                resultado.precoCall());

        System.out.printf("Preço da opção Put: %.2f%n",
                resultado.precoPut());
    }

    /*
     * IMPRIMIR A ARVORE
     *
     * Exibe os precos possiveis do ativo em cada periodo.
     * Isso ajuda a visualizar os cenarios calculados.
     */
    private void imprimirArvore(double[][] arvore) {
        System.out.println("Arvore binomial de precos:");

        for (int i = 0; i < arvore.length; i++) {
            System.out.print("Periodo " + i + ": ");

            for (int j = 0; j <= i; j++) {
                System.out.printf("%.4f ", arvore[i][j]);
            }

            System.out.println();
        }
    }

    public static void main(String[] args) {
        PrecificacaoController controller = new PrecificacaoController();

        controller.demonstrarArvoreBinomial();
        System.out.println();
        controller.demonstrarCoxRossRubinstein();
    }
}
