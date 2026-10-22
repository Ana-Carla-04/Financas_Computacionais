package br.edu.ufersa.Financas_Computacionais.features.interpolacao;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class InterpolacaoDomainService {

    //Método lagrange
    public double lagrange(List<Double> x, List<Double> y, double valor) {
        validarDados(x, y, valor);

        // Guarda a quantidade de pontos informados.
        // Como cada prazo possui uma taxa correspondente, as listas devem ter o mesmo tamanho.
        int n = x.size();
        double resultado = 0.0;

        // Percorre cada ponto conhecido para calcular seu termo no polinômio.
        for (int i = 0; i < n; i++) {
            double termo = y.get(i); // pega a posição atual de y
            for (int j = 0; j < n; j++) {  // Percorre todos os pontos para calcular os fatores do termo atual.
                if (i != j) { // Ignora o próprio ponto, pois i == j faria o denominador ser zero.
                    termo *= (valor - x.get(j)) / (x.get(i) - x.get(j));
                }
            }
            resultado += termo; // Soma os termos
        }
        validarResultado(resultado);
        return resultado;
    }

    public double newton(List<Double> x, List<Double> y, double valor) {
        validarDados(x, y, valor);

        int n = x.size(); // Guarda a quantidade de pontos conhecidos.
        double[] diferencas = new double[n]; // Armazena as taxas e diferencas

        // Copia as taxas conhecidas para o vetor auxiliar.
        // Inicialmente, cada posição contém o valor y correspondente
        for (int i = 0; i < n; i++) {
            diferencas[i] = y.get(i);
        }

        // Calcula as diferenças divididas, começando pela primeira ordem até a ordem necessária.
        for (int ordem = 1; ordem < n; ordem++) {
            for (int i = n - 1; i >= ordem; i--) {
                diferencas[i] = (diferencas[i] - diferencas[i - 1]) / (x.get(i) - x.get(i - ordem)); //fórmula
            }
        }

        // Avalia o polinômio de Newton.
        double resultado = diferencas[n - 1];

        // Percorre os coeficientes em ordem decrescente para avaliar o polinômio no prazo desejado.
        for (int i = n - 2; i >= 0; i--) {
            resultado = diferencas[i] + (valor - x.get(i)) * resultado; // formula do polinômio
        }

        validarResultado(resultado);
        return resultado;
    }

    private void validarDados(List<Double> x, List<Double> y, double valor) {
        if (x == null || y == null) {
            throw new IllegalArgumentException("As listas de prazos e taxas são obrigatórias.");
        }
        if (x.size() < 2) {
            throw new IllegalArgumentException("Informe pelo menos dois pontos.");
        }
        if (x.size() != y.size()) {
            throw new IllegalArgumentException("As listas de prazos e taxas devem ter o mesmo tamanho.");
        }
        if (!Double.isFinite(valor)) {
            throw new IllegalArgumentException("O prazo desejado deve ser um número finito.");
        }

        for (int i = 0; i < x.size(); i++) {
            Double xi = x.get(i);
            Double yi = y.get(i);

            if (xi == null || yi == null) {
                throw new IllegalArgumentException("Os prazos e as taxas não podem conter valores nulos.");
            }
            if (!Double.isFinite(xi) || !Double.isFinite(yi)) {
                throw new IllegalArgumentException("Os prazos e as taxas devem ser números finitos.");
            }
            for (int j = 0; j < i; j++) {
                // comparação primitiva: trata 0.0 == -0.0 como repetido
                if (xi.doubleValue() == x.get(j).doubleValue()) {
                    throw new IllegalArgumentException("Não podem existir prazos repetidos.");
                }
            }
        }
    }

    private void validarResultado(double resultado) {
        if (!Double.isFinite(resultado)) {
            throw new ArithmeticException(
                    "Não foi possível calcular um resultado finito.");
        }
    }
}

