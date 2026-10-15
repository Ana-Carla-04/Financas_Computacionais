
package br.edu.ufersa.Financas_Computacionais.precificacao;

public class ArvoreBinomial {

    /*
     * O QUE E UMA ARVORE BINOMIAL?
     *
     * E um metodo numerico usado para estimar o preco de uma
     * opcao financeira.
     *
     * Uma opcao e um contrato que da o direito de comprar ou
     * vender um ativo por um preco previamente estabelecido.
     *
     * A arvore considera duas possibilidades para o preco do
     * ativo em cada periodo:
     * 1. O preco sobe.
     * 2. O preco cai.
     *
     * Os cenarios sao construidos ate o vencimento da opcao.
     * Depois, o algoritmo calcula os valores de tras para frente
     * para encontrar o preco da opcao hoje.
     */

    /*
     * CONSTRUIR A ARVORE DE PRECOS
     *
     * S0 = preco atual do ativo;
     * u  = fator de alta. Exemplo: 1.10 significa alta de 10%;
     * d  = fator de baixa. Exemplo: 0.90 significa queda de 10%;
     * N  = numero de periodos considerados.
     *
     * A matriz guarda o preco possivel do ativo em cada periodo.
     * A linha i representa o periodo.
     * A coluna j representa a quantidade de movimentos de alta.
     *
     * Formula:
     * S(i,j) = S0 * u^j * d^(i-j)
     */
    public static double[][] construirArvore(
            double S0, double u, double d, int N) {

        if (S0 <= 0 || u <= 0 || d <= 0 || u <= d || N <= 0) {
            throw new IllegalArgumentException(
                    "Parametros invalidos para construir a arvore."
            );
        }

        double[][] arvore = new double[N + 1][N + 1];

        // O preco no periodo zero e o preco atual.
        arvore[0][0] = S0;

        // Calcula os precos possiveis em cada periodo.
        for (int i = 1; i <= N; i++) {
            for (int j = 0; j <= i; j++) {
                arvore[i][j] =
                        S0 * Math.pow(u, j) * Math.pow(d, i - j);
            }
        }

        return arvore;
    }

    /*
     * PRECIFICAR UMA CALL (OPCAO DE COMPRA)
     *
     * K = preco de exercicio: preco pelo qual o titular pode
     * comprar o ativo.
     *
     * No vencimento, a call vale:
     * max(S - K, 0)
     *
     * Se o ativo custar mais que K, a opcao tem valor.
     * Caso contrario, seu valor de exercicio e zero.
     *
     * r = taxa livre de risco anual;
     * T = tempo ate o vencimento, em anos;
     * p = probabilidade neutra ao risco de alta.
     */
    public static double precificarCall(
            double S0, double K, double r, double T,
            int N, double u, double d, double p) {

        validarParametros(S0, K, r, T, N, u, d, p);

        double deltaT = T / N;

        double[][] arvore = construirArvore(S0, u, d, N);

        // Primeiro calculamos os valores da opcao no vencimento.
        double[] valores = new double[N + 1];

        for (int j = 0; j <= N; j++) {
            double precoAtivo = arvore[N][j];

            // Valor da call no vencimento.
            valores[j] = Math.max(precoAtivo - K, 0.0);
        }

        /*
         * INDUCAO RETROATIVA
         *
         * Voltamos do ultimo periodo ate o periodo zero.
         *
         * Em cada no, calculamos a media ponderada dos valores
         * possiveis no proximo periodo e descontamos esse valor
         * pela taxa livre de risco.
         *
         * p = probabilidade neutra ao risco de alta;
         * (1-p) = probabilidade neutra ao risco de baixa.
         */
        for (int i = N - 1; i >= 0; i--) {
            for (int j = 0; j <= i; j++) {

                double valorEsperado =
                        p * valores[j + 1]
                                + (1.0 - p) * valores[j];

                valores[j] =
                        Math.exp(-r * deltaT) * valorEsperado;
            }
        }

        // O primeiro elemento agora contem o preco atual da call.
        return valores[0];
    }

    /*
     * PRECIFICAR UMA PUT (OPCAO DE VENDA)
     *
     * A put da ao titular o direito de vender o ativo pelo
     * preco de exercicio K.
     *
     * No vencimento, a put vale:
     * max(K - S, 0)
     */
    public static double precificarPut(
            double S0, double K, double r, double T,
            int N, double u, double d, double p) {

        validarParametros(S0, K, r, T, N, u, d, p);

        double deltaT = T / N;

        double[][] arvore = construirArvore(S0, u, d, N);

        // Calcula o valor da put em cada cenario do vencimento.
        double[] valores = new double[N + 1];

        for (int j = 0; j <= N; j++) {
            double precoAtivo = arvore[N][j];

            valores[j] = Math.max(K - precoAtivo, 0.0);
        }

        // Volta do vencimento ate hoje.
        for (int i = N - 1; i >= 0; i--) {
            for (int j = 0; j <= i; j++) {

                double valorEsperado =
                        p * valores[j + 1]
                                + (1.0 - p) * valores[j];

                valores[j] =
                        Math.exp(-r * deltaT) * valorEsperado;
            }
        }

        return valores[0];
    }

    /*
     * PROBABILIDADE NEUTRA AO RISCO
     *
     * Esta probabilidade e utilizada para precificar a opcao
     * no modelo binomial.
     *
     * Ela nao representa necessariamente a probabilidade real
     * de o ativo subir no mercado.
     *
     * Formula:
     * p = (exp(r * deltaT) - d) / (u - d)
     *
     * Este metodo e auxiliar. No projeto, tambem podera ser
     * utilizado pela classe CoxRossRubinstein.
     */
    public static double calcularProbabilidade(
            double r, double T, int N, double u, double d) {

        if (T <= 0 || N <= 0 || u <= d || d <= 0) {
            throw new IllegalArgumentException(
                    "Parametros invalidos para calcular a probabilidade."
            );
        }

        double deltaT = T / N;

        double p =
                (Math.exp(r * deltaT) - d) / (u - d);

        if (p < 0 || p > 1 || !Double.isFinite(p)) {
            throw new IllegalArgumentException(
                    "Probabilidade invalida. Verifique os parametros "
                            + "do modelo e a condicao de ausencia de arbitragem."
            );
        }

        return p;
    }

    /*
     * IMPRIMIR A ARVORE
     *
     * Exibe os precos possiveis do ativo em cada periodo.
     * Isso ajuda a visualizar os cenarios calculados.
     */
    public static void imprimirArvore(double[][] arvore) {
        System.out.println("Arvore binomial de precos:");

        for (int i = 0; i < arvore.length; i++) {
            System.out.print("Periodo " + i + ": ");

            for (int j = 0; j <= i; j++) {
                System.out.printf("%.4f ", arvore[i][j]);
            }

            System.out.println();
        }
    }

    // Valida os parametros usados na precificacao.
    private static void validarParametros(
            double S0, double K, double r, double T,
            int N, double u, double d, double p) {

        if (!Double.isFinite(S0) || S0 <= 0) {
            throw new IllegalArgumentException(
                    "O preco inicial deve ser positivo."
            );
        }

        if (!Double.isFinite(K) || K <= 0) {
            throw new IllegalArgumentException(
                    "O preco de exercicio deve ser positivo."
            );
        }

        if (!Double.isFinite(r)) {
            throw new IllegalArgumentException(
                    "A taxa livre de risco deve ser finita."
            );
        }

        if (!Double.isFinite(T) || T <= 0 || N <= 0) {
            throw new IllegalArgumentException(
                    "O vencimento e o numero de periodos devem ser positivos."
            );
        }

        if (!Double.isFinite(u) || !Double.isFinite(d)
                || u <= d || d <= 0) {
            throw new IllegalArgumentException(
                    "Os fatores devem satisfazer u > d > 0."
            );
        }

        if (!Double.isFinite(p) || p < 0 || p > 1) {
            throw new IllegalArgumentException(
                    "A probabilidade deve estar entre 0 e 1."
            );
        }

        double deltaT = T / N;
        double crescimentoLivreDeRisco = Math.exp(r * deltaT);

        if (!Double.isFinite(crescimentoLivreDeRisco)
                || d >= crescimentoLivreDeRisco
                || crescimentoLivreDeRisco >= u) {
            throw new IllegalArgumentException(
                    "Os parametros nao satisfazem a condicao "
                            + "usual de ausencia de arbitragem: "
                            + "d < exp(r * deltaT) < u."
            );
        }
    }

    /*
     * TESTE DA CLASSE
     *
     * Os valores sao hipoteticos e servem para demonstrar
     * como utilizar os metodos.
     */
    public static void main(String[] args) {

        double S0 = 100.0; // preco atual do ativo: R$ 100
        double K = 100.0;  // preco de exercicio: R$ 100
        double r = 0.05;   // taxa livre de risco: 5% ao ano
        double T = 1.0;    // vencimento em um ano
        int N = 2;         // dois periodos

        double u = 1.10;   // fator de alta: 10%
        double d = 0.90;   // fator de baixa: 10%

        // Calcula a probabilidade neutra ao risco.
        double p = calcularProbabilidade(r, T, N, u, d);

        // Constroi e exibe a arvore de precos do ativo.
        double[][] arvore = construirArvore(S0, u, d, N);
        imprimirArvore(arvore);

        // Calcula os precos das duas opcoes.
        double call = precificarCall(
                S0, K, r, T, N, u, d, p
        );

        double put = precificarPut(
                S0, K, r, T, N, u, d, p
        );

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
}
