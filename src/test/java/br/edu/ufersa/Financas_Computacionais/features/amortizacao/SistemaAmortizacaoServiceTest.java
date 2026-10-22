package br.edu.ufersa.Financas_Computacionais.features.amortizacao;

import br.edu.ufersa.Financas_Computacionais.features.amortizacao.dto.AmortizacaoRequestDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SistemaAmortizacaoServiceTest {

    private static final double TOLERANCIA = 1e-8;

    @Test
    void calculaTabelaSacECondicionamentoDaIdentidade() {
        var request = new AmortizacaoRequestDTO("SAC", 100_000.0, 0.01, 3);

        var resultado = new SistemaAmortizacaoService().calcular(request);

        assertEquals("SAC", resultado.sistema());
        assertEquals(3, resultado.linhas().size());
        assertEquals(34_333.33333333333, resultado.parcelaInicial(), TOLERANCIA);
        assertEquals(2_000.0, resultado.totalJuros(), TOLERANCIA);
        assertEquals(102_000.0, resultado.totalPago(), TOLERANCIA);
        assertEquals(1.0, resultado.condicionamento().kappa(), TOLERANCIA);
    }

    @Test
        void priceUsaFormulaEscalarQuandoMatrizSingular() {
        var request = new AmortizacaoRequestDTO("PRICE", 100_000.0, 0.01, 3);

        var resultado = new SistemaAmortizacaoService().calcular(request);
        double parcelaEsperada = 100_000.0 * 0.01
            / (1.0 - Math.pow(1.01, -3));

        assertEquals("PRICE", resultado.sistema());
        assertEquals(3, resultado.linhas().size());
        assertEquals(parcelaEsperada, resultado.parcelaInicial(), TOLERANCIA);
        assertEquals(parcelaEsperada, resultado.parcelaFinal(), TOLERANCIA);
        assertEquals("Matriz Singular (Fallback)",
            resultado.condicionamento().classificacao());
        assertEquals(0.0, resultado.condicionamento().kappa(), TOLERANCIA);
        assertEquals(0.0,
            resultado.linhas().get(2).saldoFinal(), TOLERANCIA);
    }
}