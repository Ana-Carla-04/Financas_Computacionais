package br.edu.ufersa.Financas_Computacionais.features.amortizacao;

import br.edu.ufersa.Financas_Computacionais.features.amortizacao.dto.AmortizacaoRequestDTO;
import br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.exception.MatrizSingularException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    void priceRegistraFalhaConhecidaDaMatrizSingular() {
        var request = new AmortizacaoRequestDTO("PRICE", 100_000.0, 0.01, 3);

        assertThrows(MatrizSingularException.class,
                () -> new SistemaAmortizacaoService().calcular(request));
    }
}