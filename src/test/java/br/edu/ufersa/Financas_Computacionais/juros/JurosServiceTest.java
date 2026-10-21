package br.edu.ufersa.Financas_Computacionais.juros;

import br.edu.ufersa.Financas_Computacionais.features.juros.JurosService;
import br.edu.ufersa.Financas_Computacionais.features.juros.dto.JurosRequestDTO;
import br.edu.ufersa.Financas_Computacionais.features.juros.dto.JurosResponseDTO;
import br.edu.ufersa.Financas_Computacionais.features.juros.exception.ParametrosJurosInvalidosException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JurosServiceTest {

    private static final double TOL = 1e-6;

    private final JurosService service = new JurosService();

    @Test
    void jurosESmontanteSimples() {
        assertEquals(2_400.0, service.jurosSimples(10_000, 0.02, 12), TOL);
        assertEquals(12_400.0, service.montanteSimples(10_000, 0.02, 12), TOL);
    }

    @Test
    void montanteEJurosCompostos() {
        assertEquals(12_682.417946, service.montanteComposto(10_000, 0.02, 12), 1e-4);
        assertEquals(2_682.417946, service.jurosCompostos(10_000, 0.02, 12), 1e-4);
    }

    @Test
    void compostoRendeMaisQueSimplesAposUmPeriodo() {
        assertTrue(service.montanteComposto(1_000, 0.05, 10) > service.montanteSimples(1_000, 0.05, 10));
        assertEquals(service.montanteSimples(1_000, 0.05, 1),
                service.montanteComposto(1_000, 0.05, 1), TOL);
    }

    @Test
    void valorPresenteEInversoDoValorFuturo() {
        for (String regime : new String[]{"SIMPLES", "COMPOSTO"}) {
            double vf = service.valorFuturo(8_000, 0.015, 24, regime);
            assertEquals(8_000.0, service.valorPresente(vf, 0.015, 24, regime), TOL);
        }
    }

    @Test
    void taxaEquivalenteMensalParaAnual() {
        assertEquals(0.268241795, service.taxaEquivalente(0.02, 12), 1e-8);
    }

    @Test
    void calculaRespostasDaApi() {
        JurosResponseDTO vf = service.calcularValorFuturo(
                new JurosRequestDTO("composto", 10_000, 0.02, 12));
        assertEquals("VALOR_FUTURO", vf.operacao());
        assertEquals("COMPOSTO", vf.regime());
        assertEquals(12_682.417946, vf.resultado(), 1e-4);
        assertEquals(2_682.417946, vf.juros(), 1e-4);

        JurosResponseDTO vp = service.calcularValorPresente(
                new JurosRequestDTO("SIMPLES", 12_400, 0.02, 12));
        assertEquals(10_000.0, vp.resultado(), TOL);
        assertEquals(2_400.0, vp.juros(), TOL);
    }

    @Test
    void recusaParametrosInvalidos() {
        assertThrows(ParametrosJurosInvalidosException.class,
                () -> service.montanteComposto(-1, 0.02, 12));
        assertThrows(ParametrosJurosInvalidosException.class,
                () -> service.montanteComposto(100, -0.02, 12));
        assertThrows(ParametrosJurosInvalidosException.class,
                () -> service.montanteComposto(100, 0.02, -1));
        assertThrows(ParametrosJurosInvalidosException.class,
                () -> service.valorFuturo(100, 0.02, 12, "OUTRO"));
        assertThrows(ParametrosJurosInvalidosException.class,
                () -> service.montanteComposto(100, 10, 1e6));
    }
}