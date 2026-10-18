package br.edu.ufersa.Financas_Computacionais.features.avaliacao;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ComparacaoInvestimentosServiceTest {

    @Test
    void comparaProjetosPeloMaiorValorPresenteLiquido() {
        ComparacaoInvestimentosService service = new ComparacaoInvestimentosService();

        var resultado = service.comparar(
                "Projeto A", new double[]{-1000, 400, 400, 400},
                "Projeto B", new double[]{-1000, 100, 600, 600},
                0.10
        );

        assertEquals("Projeto B", resultado.recomendacao());
        assertTrue(resultado.segundo().vpl() > resultado.primeiro().vpl());
        assertTrue(resultado.primeiro().tir().isPresent());
        assertTrue(resultado.segundo().tir().isPresent());
    }
}