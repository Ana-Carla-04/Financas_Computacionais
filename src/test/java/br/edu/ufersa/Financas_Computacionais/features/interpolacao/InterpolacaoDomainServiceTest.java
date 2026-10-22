package br.edu.ufersa.Financas_Computacionais.features.interpolacao;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InterpolacaoDomainServiceTest {

    private final InterpolacaoDomainService service = new InterpolacaoDomainService();
    private static final double EPS = 1e-9;

    @Test
    void lagrangeSlide14() {
        assertEquals(4.75,
                service.lagrange(List.of(0.0, 1.0, 2.0), List.of(1.0, 3.0, 7.0), 1.5), EPS);
    }

    @Test
    void newtonSlide24() {
        assertEquals(4.75,
                service.newton(List.of(0.0, 1.0, 2.0), List.of(1.0, 3.0, 7.0), 1.5), EPS);
    }

    @Test
    void quatroPontosSlide25() {
        var x = List.of(0.0, 1.0, 2.0, 3.0);
        var y = List.of(1.0, 3.0, 7.0, 10.0);
        assertEquals(4.9375, service.newton(x, y, 1.5), EPS);
        assertEquals(4.9375, service.lagrange(x, y, 1.5), EPS);
    }

    @Test
    void passaPelosPontosOriginais() {
        var x = List.of(0.0, 1.0, 2.0);
        var y = List.of(1.0, 3.0, 7.0);
        for (int i = 0; i < x.size(); i++) {
            assertEquals(y.get(i), service.lagrange(x, y, x.get(i)), EPS);
            assertEquals(y.get(i), service.newton(x, y, x.get(i)), EPS);
        }
    }

    @Test
    void rejeitaPrazosRepetidos() {
        assertThrows(IllegalArgumentException.class,
                () -> service.lagrange(List.of(1.0, 1.0), List.of(2.0, 3.0), 1.5));
    }

    @Test
    void rejeitaNuloNaLista() {
        assertThrows(IllegalArgumentException.class,
                () -> service.newton(Arrays.asList(1.0, null), List.of(1.0, 2.0), 1.5));
    }

    @Test
    void rejeitaTamanhosDiferentes() {
        assertThrows(IllegalArgumentException.class,
                () -> service.lagrange(List.of(1.0, 2.0), List.of(1.0), 1.5));
    }
}
