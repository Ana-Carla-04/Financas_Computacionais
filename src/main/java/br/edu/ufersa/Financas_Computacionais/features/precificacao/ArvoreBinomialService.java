package br.edu.ufersa.Financas_Computacionais.features.precificacao;

import br.edu.ufersa.Financas_Computacionais.features.precificacao.dto.ArvoreBinomialRequestDTO;
import br.edu.ufersa.Financas_Computacionais.features.precificacao.exception.ArbitragemException;
import br.edu.ufersa.Financas_Computacionais.features.precificacao.exception.ParametrosPrecificacaoInvalidosException;
import br.edu.ufersa.Financas_Computacionais.features.precificacao.exception.ProbabilidadeInvalidaException;

import java.util.function.DoubleUnaryOperator;
import org.springframework.stereotype.Service;

@Service
public class ArvoreBinomialService {

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
    public double[][] construirArvore(double S0, double u, double d, int N) {

        if (S0 <= 0 || u <= 0 || d <= 0 || u <= d || N <= 0) {
            throw new ParametrosPrecificacaoInvalidosException(
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
    public double precificarCall(ArvoreBinomialRequestDTO request) {
        // Valor da call no vencimento.
        return precificar(request,
                precoAtivo -> Math.max(precoAtivo - request.K(), 0.0));
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
    public double precificarPut(ArvoreBinomialRequestDTO request) {
        // Valor da put no vencimento.
        return precificar(request,
                precoAtivo -> Math.max(request.K() - precoAtivo, 0.0));
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
    public double calcularProbabilidade(
            double r, double T, int N, double u, double d) {

        if (T <= 0 || N <= 0 || u <= d || d <= 0) {
            throw new ParametrosPrecificacaoInvalidosException(
                    "Parametros invalidos para calcular a probabilidade."
            );
        }

        double deltaT = T / N;

        double p =
                (Math.exp(r * deltaT) - d) / (u - d);

        if (p < 0 || p > 1 || !Double.isFinite(p)) {
            throw new ProbabilidadeInvalidaException(
                    "Probabilidade invalida. Verifique os parametros "
                            + "do modelo e a condicao de ausencia de arbitragem."
            );
        }

        return p;
    }

    /*
     * Calculo comum a call e a put. As duas so diferem no valor da opcao
     * no vencimento, que chega aqui como "pagamentoNoVencimento": recebe o
     * preco do ativo e devolve quanto a opcao vale.
     *
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
    private double precificar(ArvoreBinomialRequestDTO request,
                              DoubleUnaryOperator pagamentoNoVencimento) {

        validarParametros(request);

        double deltaT = request.T() / request.N();

        double[][] arvore = construirArvore(
                request.S0(), request.u(), request.d(), request.N());

        // Primeiro calculamos os valores da opcao no vencimento.
        double[] valores = new double[request.N() + 1];

        for (int j = 0; j <= request.N(); j++) {
            double precoAtivo = arvore[request.N()][j];

            valores[j] = pagamentoNoVencimento.applyAsDouble(precoAtivo);
        }

        // Volta do vencimento ate hoje.
        for (int i = request.N() - 1; i >= 0; i--) {
            for (int j = 0; j <= i; j++) {

                double valorEsperado =
                        request.p() * valores[j + 1]
                                + (1.0 - request.p()) * valores[j];

                valores[j] =
                        Math.exp(-request.r() * deltaT) * valorEsperado;
            }
        }

        // O primeiro elemento agora contem o preco atual da opcao.
        return valores[0];
    }

    // Valida os parametros usados na precificacao.
    private void validarParametros(ArvoreBinomialRequestDTO request) {

        double S0 = request.S0();
        double K = request.K();
        double r = request.r();
        double T = request.T();
        int N = request.N();
        double u = request.u();
        double d = request.d();
        double p = request.p();

        if (!Double.isFinite(S0) || S0 <= 0) {
            throw new ParametrosPrecificacaoInvalidosException(
                    "O preco inicial deve ser positivo."
            );
        }

        if (!Double.isFinite(K) || K <= 0) {
            throw new ParametrosPrecificacaoInvalidosException(
                    "O preco de exercicio deve ser positivo."
            );
        }

        if (!Double.isFinite(r)) {
            throw new ParametrosPrecificacaoInvalidosException(
                    "A taxa livre de risco deve ser finita."
            );
        }

        if (!Double.isFinite(T) || T <= 0 || N <= 0) {
            throw new ParametrosPrecificacaoInvalidosException(
                    "O vencimento e o numero de periodos devem ser positivos."
            );
        }

        if (!Double.isFinite(u) || !Double.isFinite(d)
                || u <= d || d <= 0) {
            throw new ParametrosPrecificacaoInvalidosException(
                    "Os fatores devem satisfazer u > d > 0."
            );
        }

        if (!Double.isFinite(p) || p < 0 || p > 1) {
            throw new ProbabilidadeInvalidaException(
                    "A probabilidade deve estar entre 0 e 1."
            );
        }

        double deltaT = T / N;
        double crescimentoLivreDeRisco = Math.exp(r * deltaT);

        if (!Double.isFinite(crescimentoLivreDeRisco)
                || d >= crescimentoLivreDeRisco
                || crescimentoLivreDeRisco >= u) {
            throw new ArbitragemException(
                    "Os parametros nao satisfazem a condicao "
                            + "usual de ausencia de arbitragem: "
                            + "d < exp(r * deltaT) < u."
            );
        }
    }
}
