package br.edu.ufersa.Financas_Computacionais.features.metodosNumericos;

import br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.dto.ClassificacaoSistemaResponseDTO;
import br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.dto.EliminacaoGaussResponseDTO;
import br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.exception.MatrizSingularException;
import br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.exception.PivoNuloException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EliminacaoGaussServiceTest {

    private static final double TOL = 1e-9;

    private final EliminacaoGaussService service = new EliminacaoGaussService();

    @Test
    void resolveSemPivoteamentoExemploDoSlide() {
        double[][] A = {{2, 1, -1}, {-3, -1, 2}, {-2, 1, 2}};
        double[] b = {8, -11, -3};

        EliminacaoGaussResponseDTO r = service.resolver(A, b, false);

        assertArrayEquals(new double[]{2, 3, -1}, r.solucao(), TOL);
        assertFalse(r.pivoteamento());
        assertTrue(r.trocas().isEmpty());
        assertEquals(-1.0, r.matrizTriangular()[2][2], TOL);
        assertEquals(0.5, r.matrizTriangular()[1][1], TOL);
        assertEquals(-1.5, r.multiplicadores().get(0).valor(), TOL);
        assertEquals(4.0, r.multiplicadores().get(2).valor(), TOL);
    }

    @Test
    void resolveComPivoteamentoExemploDoSlide() {
        double[][] A = {{0, 1, 1}, {2, 1, -1}, {1, 3, -1}};
        double[] b = {5, 1, 4};

        EliminacaoGaussResponseDTO r = service.resolver(A, b, true);

        assertArrayEquals(new double[]{1, 2, 3}, r.solucao(), TOL);
        assertEquals(2, r.trocas().size());
        assertEquals(1.2, r.matrizTriangular()[2][2], TOL);
        assertEquals(3.6, r.matrizTriangular()[2][3], TOL);
    }

    @Test
    void semPivoteamentoFalhaQuandoPrimeiroPivoENulo() {
        double[][] A = {{0, 1, 1}, {2, 1, -1}, {1, 3, -1}};
        double[] b = {5, 1, 4};

        assertThrows(PivoNuloException.class, () -> service.resolver(A, b, false));
    }

    @Test
    void comPivoteamentoRecusaMatrizSingular() {
        double[][] A = {{1, 1}, {2, 2}};
        double[] b = {2, 4};

        assertThrows(MatrizSingularException.class, () -> service.resolver(A, b, true));
    }

    @Test
    void calculaDeterminanteEInversa() {
        double[][] B = {{2, 1}, {1, 3}};

        assertEquals(5.0, service.determinante(B), TOL);
        assertEquals(0.0, service.determinante(new double[][]{{1, 1}, {2, 2}}), TOL);

        double[][] inv = service.inversa(B);
        assertArrayEquals(new double[]{0.6, -0.2}, inv[0], TOL);
        assertArrayEquals(new double[]{-0.2, 0.4}, inv[1], TOL);
    }

    @Test
    void determinanteMudaDeSinalComTrocaDeLinhas() {
        assertEquals(-1.0, service.determinante(new double[][]{{0, 1}, {1, 0}}), TOL);
    }

    @Test
    void classificaSpdSpiESi() {
        double[][] bem = {{2, 1}, {1, 3}};
        double[][] singular = {{1, 1}, {2, 2}};

        ClassificacaoSistemaResponseDTO spd = service.classificar(bem, new double[]{3, 4});
        ClassificacaoSistemaResponseDTO spi = service.classificar(singular, new double[]{2, 4});
        ClassificacaoSistemaResponseDTO si = service.classificar(singular, new double[]{2, 5});

        assertEquals("SPD", spd.tipo());
        assertEquals(2, spd.posto());
        assertEquals("SPI", spi.tipo());
        assertEquals(1, spi.posto());
        assertEquals("SI", si.tipo());
    }
}