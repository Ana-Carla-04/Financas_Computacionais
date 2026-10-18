package br.edu.ufersa.Financas_Computacionais.features.metodosNumericos;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MetodosNumericosServiceTest {

    private static final double TOLERANCIA = 1e-8;

    @Test
    void fatoracaoLuResolveSistemaConhecido() {
        double[][] matriz = {
                {2, 3, 1},
                {4, 7, 3},
                {6, 18, 5}
        };
        double[] termosIndependentes = {11, 27, 57};

        double[] solucao = new FatoracaoLUService()
                .resolverSistema(matriz, termosIndependentes);

        assertArrayEquals(new double[]{1, 2, 3}, solucao, TOLERANCIA);
    }

    @Test
    void fatoracaoCholeskyResolveSistemaSimetricoDefinidoPositivo() {
        double[][] matriz = {
                {4, 12, -16},
                {12, 37, -43},
                {-16, -43, 98}
        };
        double[] termosIndependentes = {-20, -43, 192};

        double[] solucao = new FatoracaoCholeskyService()
                .resolverSistema(matriz, termosIndependentes);

        assertArrayEquals(new double[]{1, 2, 3}, solucao, TOLERANCIA);
    }

    @Test
    void jacobiResolveSistemaDiagonalmenteDominante() {
        double[][] matriz = matrizDiagonalmenteDominante();
        double[] termosIndependentes = {6, 22, -10};

        double[] solucao = new JacobiService()
                .resolverSistema(matriz, termosIndependentes);

        assertArrayEquals(new double[]{1, 2, -1}, solucao, TOLERANCIA);
    }

    @Test
    void gaussSeidelResolveSistemaDiagonalmenteDominante() {
        double[][] matriz = matrizDiagonalmenteDominante();
        double[] termosIndependentes = {6, 22, -10};

        double[] solucao = new GaussSeidelService()
                .resolverSistema(matriz, termosIndependentes);

        assertArrayEquals(new double[]{1, 2, -1}, solucao, TOLERANCIA);
    }

    @Test
    void identidadeTemNumeroDeCondicionamentoUm() {
        double[][] identidade = {
                {1, 0, 0},
                {0, 1, 0},
                {0, 0, 1}
        };

        var resultado = new CondicionamentoService().calcular(identidade);

        assertEquals(1.0, resultado.numeroCondicionamento(), TOLERANCIA);
        assertEquals("Bem condicionada", resultado.classificacao());
    }

    private double[][] matrizDiagonalmenteDominante() {
        return new double[][]{
                {10, -1, 2},
                {-1, 11, -1},
                {2, -1, 10}
        };
    }
}