package br.edu.ufersa.Financas_Computacionais.features.metodosNumericos;

import br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.AnaliseEstabilidadeService.EfeitoDaEscala;
import br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.AnaliseEstabilidadeService.Sensibilidade;
import br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.dto.CondicionamentoNormasResponseDTO;
import br.edu.ufersa.Financas_Computacionais.shared.util.Normas;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NormasCondicionamentoTest {

    private static final double TOL = 1e-9;

    private final CondicionamentoService condicionamento = new CondicionamentoService();
    private final AnaliseEstabilidadeService estabilidade = new AnaliseEstabilidadeService();

    @Test
    void normasVetoriaisDoSlide() {
        double[] x = {3, -4, 12};

        assertEquals(19.0, Normas.vetorial1(x), TOL);
        assertEquals(13.0, Normas.vetorial2(x), TOL);
        assertEquals(12.0, Normas.vetorialInfinito(x), TOL);
    }

    @Test
    void normasMatriciaisDoSlide() {
        double[][] A = {{4, -2}, {1, 3}};

        assertEquals(5.0, Normas.matricial1(A), TOL);
        assertEquals(6.0, Normas.matricialInfinito(A), TOL);
        assertEquals(Math.sqrt(30.0), Normas.matricialFrobenius(A), TOL);
    }

    @Test
    void matrizBemCondicionada() {
        double[][] A = {{2, 0}, {0, 4}};

        CondicionamentoNormasResponseDTO r = condicionamento.calcular(A, TipoNorma.INFINITO);

        assertEquals(4.0, r.normaMatriz(), TOL);
        assertEquals(0.5, r.normaInversa(), TOL);
        assertEquals(2.0, r.numeroCondicionamento(), TOL);
        assertEquals("Bem condicionada", r.classificacao());
    }

    @Test
    void matrizMalCondicionadaDoSlide() {
        double[][] A = {{1, 1}, {1, 1.0001}};

        CondicionamentoNormasResponseDTO r = condicionamento.calcular(A, TipoNorma.INFINITO);

        assertEquals(40004.0001, r.numeroCondicionamento(), 1e-3);
        assertEquals(4, r.digitosPerdidos());
        assertTrue(condicionamento.calcular(A, TipoNorma.UM).numeroCondicionamento() > 1e4);
        assertTrue(condicionamento.calcular(A, TipoNorma.FROBENIUS).numeroCondicionamento() > 1e4);
    }

    @Test
    void matrizBemCondicionadaDoContraste() {
        double[][] B = {{2, 1}, {1, 3}};

        assertEquals(3.2, condicionamento.calcular(B, TipoNorma.INFINITO).numeroCondicionamento(), TOL);
    }

    @Test
    void perturbacaoDoSlideRespeitaOLimitante() {
        double[][] A = {{1, 1}, {1, 1.0001}};
        double[] b = {2, 2.0001};
        double[] deltaB = {0, 0.0001};

        Sensibilidade s = estabilidade.analisarPerturbacao(A, b, deltaB);

        assertArrayEquals(new double[]{1, 1}, s.solucao(), 1e-6);
        assertArrayEquals(new double[]{0, 2}, s.solucaoPerturbada(), 1e-6);
        assertEquals(1.0, s.erroRelativoSolucao(), 1e-6);
        assertEquals(2.0001, s.limitante(), 1e-3);
        assertTrue(s.limitanteRespeitado());
    }

    @Test
    void escalarAMatrizMudaODeterminanteMasNaoOKappa() {
        double[][] A = {{1, 1}, {1, 1.0001}};

        EfeitoDaEscala e = estabilidade.compararEscala(A, 0.001);

        assertEquals(1.0e-4, e.determinanteOriginal(), 1e-9);
        assertEquals(1.0e-10, e.determinanteEscalado(), 1e-15);
        assertEquals(e.kappaOriginal(), e.kappaEscalado(), 1e-3);
    }
}