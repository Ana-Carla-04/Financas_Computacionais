package br.edu.ufersa.Financas_Computacionais.features.interpolacao;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class InterpolacaoDomainService {

    public double lagrange(
            List<Double> x,
            List<Double> y,
            double valor
    ) {
        int n = x.size();
        double resultado = 0.0;

        for (int i = 0; i < n; i++) {
            double termo = y.get(i);

            for (int j = 0; j < n; j++) {
                if (i != j) {
                    termo *= (valor - x.get(j))
                            / (x.get(i) - x.get(j));
                }
            }

            resultado += termo;
        }

        return resultado;
    }

    public double newton(
            List<Double> x,
            List<Double> y,
            double valor
    ) {
        int n = x.size();
        double[] diferencas = new double[n];

        for (int i = 0; i < n; i++) {
            diferencas[i] = y.get(i);
        }

        // Calcula as diferenças divididas.
        for (int ordem = 1; ordem < n; ordem++) {
            for (int i = n - 1; i >= ordem; i--) {
                diferencas[i] =
                        (diferencas[i] - diferencas[i - 1])
                                / (x.get(i) - x.get(i - ordem));
            }
        }

        // Avalia o polinômio de Newton.
        double resultado = diferencas[n - 1];

        for (int i = n - 2; i >= 0; i--) {
            resultado = diferencas[i]
                    + (valor - x.get(i)) * resultado;
        }

        return resultado;
    }
}

