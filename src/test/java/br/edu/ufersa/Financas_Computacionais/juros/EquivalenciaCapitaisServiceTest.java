package br.edu.ufersa.Financas_Computacionais.juros;

import br.edu.ufersa.Financas_Computacionais.features.juros.EquivalenciaCapitaisService;
import br.edu.ufersa.Financas_Computacionais.features.juros.dto.EquivalenciaCapitaisResponseDTO;
import br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.exception.MatrizSingularException;
import org.junit.jupiter.api.Test;

import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EquivalenciaCapitaisServiceTest {

    private final EquivalenciaCapitaisService service = new EquivalenciaCapitaisService();

    @Test
    void resolveEAtingeAsMetas() {
        double[] taxas = {0.01, 0.03};
        double[] prazos = {12, 24};
        double[] metas = {5_000, 9_000};

        EquivalenciaCapitaisResponseDTO r = service.calcular(taxas, prazos, metas);

        for (int m = 0; m < metas.length; m++) {
            double total = 0.0;
            for (int j = 0; j < taxas.length; j++) {
                total += r.aplicacoes()[j] * Math.pow(1 + taxas[j], prazos[m]);
            }
            assertEquals(metas[m], total, 1e-6);
        }
        assertTrue(r.residuo() < 1e-6);
        assertTrue(r.numeroCondicionamento() < 1e3);
    }

    @Test
    void taxasQuaseIguaisDeixamOSistemaMalCondicionado() {
        EquivalenciaCapitaisResponseDTO distintas = service.calcular(
                new double[]{0.01, 0.03}, new double[]{12, 24}, new double[]{5_000, 9_000});
        EquivalenciaCapitaisResponseDTO parecidas = service.calcular(
                new double[]{0.01, 0.0101}, new double[]{12, 24}, new double[]{5_000, 9_000});

        assertTrue(parecidas.numeroCondicionamento() > 100 * distintas.numeroCondicionamento());
        assertTrue(parecidas.numeroCondicionamento() > 1e3);
    }

    @Test
    void taxasIguaisTornamAMatrizSingular() {
        assertThrows(MatrizSingularException.class, () -> service.calcular(
                new double[]{0.02, 0.02}, new double[]{12, 24}, new double[]{5_000, 9_000}));
    }
}